#!/bin/bash

#IF YOU USE THIS SCRIPT, PLEASE CHECK IF THE PATH TO THE PROJECT TO BUILD IS CORRECT
echo "CHECK THE PATH CONTAINED IN THE BASE VARIABLE OF THIS SCRIPT IN CASE OF ERROR!!"
BASE="$HOME/Developer/GitHub/TicketGenius"

cd "$BASE" || exit
cd client || exit
rm -rf ./node_modules ./build -y
npm install --silent && npm run build

cd ../ticketingServer || exit
rm -rf ./src/main/resources/static/* -y
cp -r ../client/build/* ./src/main/resources/static/.

cd ..
docker build -t ticketing_server . --no-cache
docker compose up -d
#cd ./client && npm start
