FROM node:18
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN rm -f .env
EXPOSE 5000
CMD ["node", "src/index.js"]
