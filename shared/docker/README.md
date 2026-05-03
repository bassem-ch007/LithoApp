# LithoApp Docker Images

The Dockerfiles for application images are centralized under `shared/docker` so CI/CD, release automation, and server deployment can use one predictable Docker location without mixing pipeline concerns into service source folders.

The Docker build context is the project root. That keeps each Dockerfile able to copy exactly the service or frontend files it needs while still being invoked from GitHub Actions or a deployment machine with the same command shape.

The project-root `.dockerignore` is what standard `docker build ... .` commands use to keep `.git`, build outputs, logs, and dependency folders out of the uploaded build context. The `.dockerignore` files stored beside these Dockerfiles document the same intended exclusions for each image directory.

## Local Image Builds

Run these commands from the project root:

```bash
docker build -f shared/docker/api-gateway/Dockerfile -t lithoapp-api-gateway:test .
docker build -f shared/docker/patient-service/Dockerfile -t lithoapp-patient-service:test .
docker build -f shared/docker/episode-service/Dockerfile -t lithoapp-episode-service:test .
docker build -f shared/docker/analysis-service/Dockerfile -t lithoapp-analysis-service:test .
docker build -f shared/docker/drainage-service/Dockerfile -t lithoapp-drainage-service:test .
docker build -f shared/docker/frontend/Dockerfile -t lithoapp-frontend:test .
```

## CI/CD Readiness

This layout is ready for GitHub Actions jobs that check out the repository, build each image from the project root, tag images for Docker Hub, and push them after tests pass. A typical pipeline can keep service-specific build commands explicit while sharing common credentials, Docker Buildx setup, and tag naming conventions.

For Docker Hub publishing, the same builds can be tagged with repository names such as:

```bash
docker tag lithoapp-api-gateway:test <dockerhub-user>/lithoapp-api-gateway:<version>
docker push <dockerhub-user>/lithoapp-api-gateway:<version>
```

Repeat the same tag and push pattern for the patient, episode, analysis, drainage, and frontend images.

## Server Image Retention

Do not run `docker system prune -a` on the server. It can remove older application images that are useful for fast rollbacks after a failed deployment. Prefer targeted cleanup of dangling layers or explicitly obsolete tags once rollback windows have passed.
