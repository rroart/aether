import requests
import os

aport = os.environ.get('MYAPORT')
if aport is None:
    aport = "80"
    aport = "23456"

ahost = os.environ.get('MYAHOST')
if ahost is None:
    ahost = "localhost"
    
url1 = 'http://' + ahost + ':' + aport + '/'

#headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
#headers={'Content-type':'application/json', 'Accept':'application/json'}
headers={'Content-Type' : 'application/json;charset=utf-8'}
def request1(param, webpath):
    return requests.post(url1 + webpath, json=param, headers=headers)

def request0(data):
    return requests.post(url, data='', headers=headers)
    #return requests.post(url, data=json.dumps(data), headers=headers)

