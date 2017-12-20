import requests

r = requests.put('https://cardsec.cfapps.io/api/panels/request?panelId=ad9611b0-c759-4714-872e-2ed55e70ec80&cardId=SHANE14&allowed=true')
r = requests.put('https://cardsec.cfapps.io/api/panels/request?panelId=066e6fd3-49a4-4e08-8793-eb87bd323408&cardId=SHANE14&allowed=true')

print(r.json())
