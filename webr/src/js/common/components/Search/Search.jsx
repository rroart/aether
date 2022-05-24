import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, ButtonToolbar, Nav, Navbar, NavItem, FormControl } from 'react-bootstrap';
import SearchBar from './SearchBar';
import { MyTable } from '../MyTable'
import { Table } from '../Table'
import { constants as mainConstants, actions as mainActions } from '../../../redux/modules/main';
import { memo, useCallback, useEffect, useMemo, useState } from "react";
import { useTable } from 'react-table';
import ReactTooltip from "react-tooltip";

function Search({dolucene, dosolr, doelastic, props}) {
    const [ htm, setHtm ] = useState(<div><h1>No table</h1></div>);
    const [ hcolumns, setHcolumns ] = useState(null);
    const [ hcolumns2, setHcolumns2 ] = useState(null);
    const [ hdata, setHdata ] = useState(null);
    const [ hdata2, setHdata2 ] = useState(null);
    const [ result2, setResult2 ] = useState({ list : [[]]});
    const [ searchmlt, setSearchmlt ] = useState("");
    console.log(props);
    const callback = useCallback((hcolumns, hdata) => {
	console.log("callback");
	console.log(hcolumns);
	console.log(hdata);
	setHcolumns(hcolumns);
	setHdata(hdata);
	/*
    const {
	getTableProps,
	getTableBodyProps,
	headerGroups,
	rows,
	prepareRow,
    } = useTable({ columns: hcolumns, data: hdata });
    const table = MyTable.gethtable(getTableProps, getTableBodyProps, headerGroups, rows, prepareRow);
	const tables = [];
    tables.push(table);
    console.log(table);
    const htm = (
	<div>
    	    { tables.map(item => item) }
	</div>
    );
*/
/*		
	setHtm(<div>
		     <Table hcolumns={hcolumns} hdata={hdata} />
	       </div>);
	console.log("call " + htm);
mainActions.newtabMain(htm);
	console.log("call " + htm);
*/
//mainActions.newtabMain( <Table hcolumns={hcolumns} hdata={hdata} />);
    }, []);

    const callback2 = useCallback((result2) => {
	console.log("callback2");
	//setResult2(result2);
	setSearchmlt(result2);
	//setHcolumns2(hcolumns2);
	//setHdata2(hdata2);
    }, []);

    const callback3 = useCallback((result2) => {
    }, []);

    useEffect(() => {
	if (searchmlt === "") {
	    return;
	}
	const result = Client.fetchApi.search("/searchmlt", { str : searchmlt });
	result.then(function(result) {
	    const list = result.list;
	    console.log(result);
	    console.log(list);
	    const tables = [];
	    //for(var i = 0; i < list.length; i++) {
	    const i = 0;
	    const resultitemtable = list[i];
	    const baseurl = Client.geturl("/");
	    
	    const mycolumns = MyTable.getcolumns(resultitemtable, baseurl, callback2);
	    const mydata = MyTable.getdata(resultitemtable);
	    setHcolumns2(mycolumns);
	    setHdata2(mydata);
	});
	console.log("callback2", result2);
	// nei. const u = useMemo( () => []); //, [mycolumns] );
	console.log("callback2", result2);
    }, [searchmlt]);

    useEffect(() => {
    }, [result2]);
    
    console.log("call " + htm);

    const list = result2.list;
    console.log(result2);
    console.log(list);
    const tables = [];
    //for(var i = 0; i < list.length; i++) {
    const i = 0;
    const resultitemtable = list[i];
    const baseurl = Client.geturl("/");
    const mycolumns = MyTable.getcolumns(resultitemtable, baseurl, callback3);
    console.log(resultitemtable);
    console.log(mycolumns);
    const mydata = MyTable.getdata(resultitemtable);
    //const myhcolumns = useMemo( () => mycolumns, [result2] );
    //const myhdata = useMemo( () => mydata, [result2] );
    //console.log(myhcolumns);
    //console.log(hdata);
    if (mycolumns != null && mydata != null && mycolumns.length > 0 && mydata.length > 0) {
    setHcolumns2(mycolumns);
    setHdata2(mydata);
    }
    
const main = props;
      //this.bardvd = new SearchBar('dvd');
      //console.log(Object.keys(main))
      //console.log(main.config);
      //console.log(typeof main.config);
      //console.log(Object.keys(main.config));
      //console.log(main.config.keys());
      //console.log(main.config.text);
      //console.log(main.config.get("_root"));
      //console.log(main.config.get("conf"));
      //console.log(main.config.get("text"));
      //console.log(main.config.conf);
      //console.log(Object.keys(main.config.get('configValueMap')));
      //console.log(main.config.configValueMap);
      //console.log(main.config['configValueMap']);
      //console.log(main.config.get('configValueMap'));
      //console.log(main.config.get('configValueMap').get("searchengine.lucene"));
      //console.log(main.config.get('configValueMap').get("searchengine.lucene[@enable]"));
      //console.log(main.config.get('configValueMap').get("database.cassandra[@enable]"));
      //console.log(main.config.get('configValueMap')["database.cassandra[@enable]"]);
      //console.log(main.config.get('configValueMap').get("searchengine.lucene[@enable]"));
    let Searchbars=<h2>Waiting for config</h2>;
    console.log(main, dolucene, dosolr, doelastic);
    //if (!!main.config) {
	/*
	  const dolucene = main.config.get('configValueMap').get("searchengine.lucene[@enable]");
	  const dosolr = main.config.get('configValueMap').get("searchengine.solr[@enable]");
	  const doelastic = main.config.get('configValueMap').get("searchengine.elastic[@enable]");
*/
	  if (dolucene) {
	      Searchbars=<div>
			     <SearchBar text='Search standard' type='0' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search analyzing' type='1' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search complexphrase' type='2' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search extendable' type='3' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search multi' type='4' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search surround' type='5' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search classic' type='6' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search simple' type='7' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			 </div>;
	  }
	  if (dosolr) {
	      Searchbars=<div>
			     <SearchBar text='Search default' type='0' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search lucene' type='1' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search complexphrase' type='2' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search surround' type='3' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			     <SearchBar text='Search simple' type='4' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			 </div>;
	  }
	  if (doelastic) {
	      Searchbars=<div>
			     <SearchBar text='Search' type='0' config={props.config} parentCallback={callback} parentCallback2={callback2}/>
			 </div>;
	  }
     // }
      return (
	  <div>
	      { Searchbars }
	      <div>
	       <ReactTooltip effect="solid" html="true"/>
		  <Table columns={hcolumns} data={hdata} />
	      </div>
	      <div>
	       <ReactTooltip effect="solid" html="true"/>
	  <Table columns={hcolumns2} data={hdata2} />
	  </div>
	      </div>
    );
}

export default memo(Search);
