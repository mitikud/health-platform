# ----- build stage -----
FROM node:20-bullseye AS build
WORKDIR /app

# Install deps
COPY package*.json ./
RUN npm ci

# Copy source
COPY .. .

# Build for production
# You can override at build time: --build-arg VITE_API_BASE_URL=https://your-gateway
ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
RUN npm run build

# ----- runtime stage -----
FROM nginx:1.27-alpine AS runtime
# Copy a minimal SPA nginx config that also proxies /api -> backend gateway
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Static files
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
