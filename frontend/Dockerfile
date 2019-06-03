FROM node:8 as build

WORKDIR /build

RUN curl -o- -L https://yarnpkg.com/install.sh | bash
RUN apt-get install -y yarn

COPY package.json yarn.lock ./
RUN yarn --pure-lockfile build

FROM httpd:2.4.39-alpine

COPY --from=build /build/build /usr/local/apache2/htdocs/
