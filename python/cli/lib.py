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

def searchmlt(search):
        import multiprocessing as mp
        mp.Process(target=searchmltP, args=(search)).start()

def searchmltP(search):
        #SearchEngineSearchParam param = SearchEngineSearchParam();
        #param.conf = getConfig();
        #param.str = search;
        #param.searchtype = type;
        param = { "str" : search }
        response = request.request1(param, "searchmlt")
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

def traverse(add = None):
        import multiprocessing as mp
        mp.Process(target=traverseP, args=(add,)).start()

def traverseP(add):
        #TraverseEngineTraverseParam param = TraverseEngineTraverseParam();
        #param.conf = getConfig();
        #param.str = traverse;
        #param.traversetype = type;
        param = { "function" : "FILESYSTEM", "add" : add }
        response = request.request1(param, "traverse")
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

def index(add = None, reindex = False):
        import multiprocessing as mp
        mp.Process(target=indexP, args=(add, reindex)).start()

def indexP(add, reindex):
        #IndexEngineIndexParam param = IndexEngineIndexParam();
        #param.conf = getConfig();
        #param.str = index;
        #param.indextype = type;
        param = { "function" : "INDEX", "add" : add, "reindex" : reindex }
        response = request.request1(param, "index")
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

def filesystemlucenenew(add, md5checknew):
        param = { "function" : "FILESYSTEMLUCENENEW", "add" : add, "md5checknew" : md5checknew }
        response = request.request1(param, "filesystemlucenenew")

def consistentclean(clean):
        param = { "function" : "CONSISTENTCLEAN", "clean" : clean }
        response = request.request1(param, "consistentclean")
        pltshow(response)

def dbcheck(db):
        param = { "function" : "DBCHECK", "name" : db }
        response = request.request1(param, "dbcheck")
        pltshow(response)

def deletepathdb(path):
        param = { "function" : "DELETEPATH", "file" : path }
        response = request.request1(param, "deletepathdb")

def overlapping():
         response = request.request1({ "function" : "OVERLAPPING" }, "overlapping" )
         
def indexclean():
        response = request.request1({ "function" : "INDEXCLEAN" }, "indexclean")

def indexdelete():
        response = request.request1({ "function" : "INDEXDELETE" }, "indexdelete")

def dbclear(db):
        response = request.request1({ "function" : "DBCLEAR", "name" : db }, "dbclear")

def dbdrop(db):
        response = request.request1({ "function" : "DBDROP", "name" : db }, "dbdrop")
        
def dbcopy(src, dst):
        response = request.request1({ "function" : "DBCOPY", "name" : src, "add" : dst }, "dbcopy")
        
def dbsearch(string = None):
        response = request.request1({ "function" : "DBSEARCH", "file" : string }, "dbsearch")
        print(response)
        print(response.json())
        
def dbindex(string = None):
        response = request.request1({ "function" : "DBINDEX", "file" : string }, "dbindex")
        print(response)
        print(response.json())
        
def notindexed():
        response = request.request1({ "function" : "NOTINDEXED" }, "notindexed")
        print(response)
        print(response.json())

def pltshow(response):
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
        
