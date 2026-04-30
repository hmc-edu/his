# 默认用 docker.io（国际通用）。
# 国内学生若 docker.io 拉不动，可在 build 时覆盖：
#   docker compose build --build-arg BASE_REGISTRY=m.daocloud.io/docker.io
ARG BASE_REGISTRY=docker.io

FROM ${BASE_REGISTRY}/library/node:20-alpine AS build
WORKDIR /app

RUN corepack enable && corepack prepare pnpm@9 --activate

COPY package.json .npmrc ./
RUN pnpm install --no-frozen-lockfile

COPY . .
RUN pnpm build

FROM ${BASE_REGISTRY}/library/nginx:1.25-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
