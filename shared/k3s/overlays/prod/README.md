# LithoApp production overlay

Set the six LithoApp application images in `kustomization.yaml` before applying.
Use immutable DockerHub tags, ideally the Git SHA produced by CI.

Example:

```bash
cd shared/k3s/overlays/prod
kustomize edit set image docker.io/bassem00/lithoapp-patient-service=docker.io/bassem00/lithoapp-patient-service:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-episode-service=docker.io/bassem00/lithoapp-episode-service:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-analysis-service=docker.io/bassem00/lithoapp-analysis-service:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-drainage-service=docker.io/bassem00/lithoapp-drainage-service:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-api-gateway=docker.io/bassem00/lithoapp-api-gateway:<git-sha>
kustomize edit set image docker.io/bassem00/lithoapp-frontend=docker.io/bassem00/lithoapp-frontend:<git-sha>
```

