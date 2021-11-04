#!/usr/bin/python3
import readline
readline.set_auto_history(False)
import request
import matplotlib.pyplot as plt
import multiprocessing as mp

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

def search(search, type = "0"):
        import multiprocessing as mp
        mp.Process(target=searchP, args=(search, type)).start()

def searchP(search, type = "0"):
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
        val1 = l[0][0]['items']
        val2 = [ 1 + i for i in range(len(l[0]) - 1 ) ]
        #for i in l:
        #    for j in i:
        #        print(j['items'])
        val3 = [ l[0][i]['items'] for i in range(1, len(l[0])) ]
        fig, ax = plt.subplots() 
        ax.set_axis_off()
        table = ax.table( 
                cellText = val3,  
                rowLabels = val2,  
                colLabels = val1, 
                cellLoc ='left',  
                loc ='upper left')
        table.auto_set_font_size(False)
        plt.show()

