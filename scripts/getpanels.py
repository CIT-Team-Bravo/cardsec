import requests

r = requests.get('http://uuidlocator.cfapps.io/api/panels/')

for panel in r.json():
	x = requests.get('http://uuidlocator.cfapps.io/api/panels/'+ panel)
	print(x.json())
