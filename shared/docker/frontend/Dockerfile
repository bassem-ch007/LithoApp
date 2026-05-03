# syntax=docker/dockerfile:1

FROM node:22-alpine AS build

WORKDIR /app
COPY frontend/frontLitho/package*.json ./
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi

COPY frontend/frontLitho/ ./
RUN npm run build -- --configuration production

FROM nginx:alpine

COPY shared/docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/front-litho/browser/ /usr/share/nginx/html/

EXPOSE 80
