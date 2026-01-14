# Multi-stage build cho React + Vite frontend

# Stage 1: Build
FROM node:20-alpine AS build

WORKDIR /app

# Copy package files
COPY package.json ./

# Install dependencies (dùng npm install để tự động tạo/update package-lock.json)
RUN npm install

# Copy source code
COPY . .

# Build application
RUN npm run build

# Stage 2: Production với Nginx
FROM nginx:alpine

# Copy built files từ build stage
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]

