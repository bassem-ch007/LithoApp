# syntax=docker/dockerfile:1

FROM node:22-alpine AS build
WORKDIR /workspace/frontend/frontLitho

COPY frontend/frontLitho/package*.json ./
RUN npm ci

COPY frontend/frontLitho/ ./
RUN npm run build -- --configuration production

FROM nginx:1.27-alpine
COPY shared/docker/prod/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/frontend/frontLitho/dist/front-litho/browser /usr/share/nginx/html

EXPOSE 80
