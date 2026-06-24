
import json
from urllib import request

BASE='http://127.0.0.1:48080'
HEADERS={'Authorization':'Bearer test1','tenant-id':'1','Content-Type':'application/json; charset=utf-8'}

def do(method, path, payload=None, params=None):
    import urllib.parse
    url=BASE+path
    if params:
        url += '?' + urllib.parse.urlencode(params, doseq=True)
    body=None
    headers=dict(HEADERS)
    if payload is not None:
        body=json.dumps(payload, ensure_ascii=False).encode('utf-8')
        headers['Content-Length']=str(len(body))
    req=request.Request(url, data=body, headers=headers, method=method)
    with request.urlopen(req, timeout=30) as resp:
        return json.loads(resp.read().decode('utf-8'))

# 1) Ensure partner apply exists then audit via admin-api later if token available
print(json.dumps(do('GET', '/app-api/member/user/profile'), ensure_ascii=False))
