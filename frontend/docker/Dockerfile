FROM node:8 as build

WORKDIR /build

RUN curl -o- -L https://yarnpkg.com/install.sh | bash

COPY package.json yarn.lock ./
RUN yarn --pure-lockfile install

COPY public public/
COPY src src/

RUN yarn --pure-lockfile build

FROM httpd:2.4.39

RUN useradd httpd

COPY ./docker/httpd.conf /usr/local/apache2/conf/httpd.conf
COPY --from=build /build/build /usr/local/apache2/htdocs/
