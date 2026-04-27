# LithoApp Kind Image Build Helpers

These scripts build the local Docker images expected by the Kubernetes manifests and load them into the Kind cluster named `lithoapp-cluster`.

## Image Names

The Kubernetes Deployments use these exact images:

```text
lithoapp/patient-service:local
lithoapp/episode-service:local
lithoapp/analysis-service:local
lithoapp/drainage-service:local
lithoapp/api-gateway:local
lithoapp/frontend:local
```

The helper scripts build and load exactly those tags.

## Build All Images

From the project root:

```sh
sh infrastructure/k8s/docker/build-images.sh
```

Equivalent commands:

```sh
docker build -t lithoapp/patient-service:local ./services/patient-service
docker build -t lithoapp/episode-service:local ./services/episode-service
docker build -t lithoapp/analysis-service:local ./services/analysis-service
docker build -t lithoapp/drainage-service:local ./services/drainage-service
docker build -t lithoapp/api-gateway:local ./services/api-gateway
docker build -t lithoapp/frontend:local ./frontend/frontLitho
```

## Load Images Into Kind

```sh
sh infrastructure/k8s/docker/load-images-kind.sh
```

By default, the scripts use `lithoapp-cluster`. To use another Kind cluster:

```sh
KIND_CLUSTER_NAME=my-cluster sh infrastructure/k8s/docker/load-images-kind.sh
```

## Build And Load

```sh
sh infrastructure/k8s/docker/build-and-load-kind.sh
kubectl apply -k infrastructure/k8s/local
```

## Rebuild One Service

```sh
docker build -t lithoapp/patient-service:local ./services/patient-service
kind load docker-image lithoapp/patient-service:local --name lithoapp-cluster
kubectl rollout restart deployment/patient-service -n lithoapp
```

Use the matching service name and image tag for the other apps:

```sh
docker build -t lithoapp/api-gateway:local ./services/api-gateway
kind load docker-image lithoapp/api-gateway:local --name lithoapp-cluster
kubectl rollout restart deployment/api-gateway -n lithoapp
```

## Restart One Deployment

```sh
kubectl rollout restart deployment/patient-service -n lithoapp
kubectl rollout status deployment/patient-service -n lithoapp
```

## Check Image Pull Errors

```sh
kubectl get pods -n lithoapp
kubectl describe pod -n lithoapp -l app=patient-service
kubectl get events -n lithoapp --sort-by=.lastTimestamp
```

For Kind local images, verify the image was loaded:

```sh
docker image ls 'lithoapp/*'
kind get clusters
kind load docker-image lithoapp/patient-service:local --name lithoapp-cluster
```

## Debug Failed Containers

```sh
kubectl logs -n lithoapp deployment/patient-service
kubectl logs -n lithoapp deployment/api-gateway
kubectl describe deployment patient-service -n lithoapp
kubectl describe pod -n lithoapp -l app=patient-service
kubectl exec -it -n lithoapp deployment/patient-service -- sh
```

For crash loops, inspect the previous container logs:

```sh
kubectl logs -n lithoapp deployment/patient-service --previous
```

