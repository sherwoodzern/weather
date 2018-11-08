
# Demonstrate how to set up the egress gateway in Istio. 

Istio-enabled services, Envoy proxy deployed as sidecar, are unable to access URLs outside of the Kubernetes cluster.  The proxy only handles intra-cluster destinations.  In order for a microservice to access an external service the iptables need to be modified to include the external endpoint.  

To add the external service to the iptables a couple of configurations need to be added.  These configurations are a service entry, an egress gateway, and a virtual service to route traffic from the proxy (sidecar) to the gateway service.

The basic microservice developed here invokes an external service (https://api.openweathermap.org/data/2.5/weather). In order for you to use this service you need to register at the URL - https://openweathermap.org/api.  This will provide you an api key, which is necessary to invoke the weather map API.  When you have the api key update the resources/application.yaml with the new api key.



## Prerequisites

1. Maven 3.5 or newer
2. Java SE 8 or newer
3. Docker 17 or newer to build and run docker images
4. Kubernetes minikube v0.24 or newer to deploy to Kubernetes (or access to a K8s 1.7.4 or newer cluster)
5. Kubectl 1.7.4 or newer to deploy to Kubernetes

Verify prerequisites
```
java --version
mvn --version
docker --version
minikube version
kubectl version --short

Update the resources/application.yaml file with the api key provided by https://openweathermap.org/api.

```

## Build

```
mvn package
```

## Start the application

```
java -jar target/weather-1.0.jar
```

## Exercise the application

```
curl -X GET -H "host: weather.com" http://localhost:8080/weather/current/zip/{zip}


```

## Build the Docker Image

```
docker build -t weather-1.0 target
```

## Start the application with Docker

```
docker run --rm -p 8080:8080 weather-1.0:latest
```

Exercise the application as described above

## Deploy the application to Kubernetes

```
kubectl cluster-info                                      # Verify which cluster
kubectl get pods                                          # Verify connectivity to cluster
kubectl apply -f src/main/k8s/weather-service-svc.yaml    # Deploy application
kubectl apply -f src/main/k8s/weather-service-v1.yaml     # Deploy version 1 of the application
kubectl get service weather-service                       # Get service info

## Set up the egress gateway

kubectl apply -f istio/egress/weather-serviceEntry.yaml
kubectl apply -f istio/egress/weather-egress.yaml
kubectl apply -f istio/egress/sidecar-to-egress.yaml

Obtain the IP of the istio-ingressgateway

kubectl get svc istio-ingressgateway -n istio-system

Invoke the weather service
curl -X -H "host: weather.com" http://<EXTERNAL-IP>/weather/current/zip/{zip}

## Place output from the request here

## Demonstrate the use of Mirror in Istio

kubectl apply -f src/main/k8s/weather-service-v2.yaml         # Deploy version 2 of the service
kubectl apply -f src/main/istio/dest-rule.yaml                # Setup a destination rule for versions v1 and v2
kubectl apply -f src/main/istio/oraclecodeone-pathroute.yaml  # Set so 100% of all requests go to version v1

## Dump the logs for the containers for weather service v1 and v2.  Only v1 should be receiving the request

kubectl logs po weather-service-v1-xxxxxxx --tail=5 -c svc

## Show the logs from the service output

kubectl logs po weather-service-v2-xxxxxxx --tail-5 -c svc

## shows that no traffic was sent to v2

## To demonstrate mirror of the payload let's deploy the yaml file to request mirror

kubectl apply -f src/main/istio/oco-weather-mirror.yaml

## Dump the logs for service v1 and v2

kubectl logs po weather-service-v1-xxxxxxx --tail=5 -c svc

## show the output

kubectl logs po weather-service-v2-xxxxxxx --tail=5 -c svc

## show the output
Can now see that v2 received the request as well.


```

