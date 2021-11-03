#!/usr/bin/python3
import readline
readline.set_auto_history(False)
import request

# class SearchEngineParam:
#     def __init__(self, webpath, nodename, conf):
#         self.webpath = webpath
#         self.nodename = nodename
#         self.conf = conf

# class SearchEngineSearchParam(SearchEngineParam):
#     def __init__(self, webpath, nodename, conf, str, searchtype):
#         SearchEngineParam.__init__(self, webpath, nodename, conf)
#         def.str = str
#         def.searchtype = searchtype

def search(search, type):
        #SearchEngineSearchParam param = SearchEngineSearchParam();
        #param.conf = getConfig();
        #param.str = search;
        #param.searchtype = type;
        param = { "str" : search, "searchtype" : type }
        response = request.request1(param, "search")
        print(response)
        print(response.json())
        print(response.json()['list'])
        l = response.json()['list']
        for i in l:
            for j in i:
                print(j['items'])


