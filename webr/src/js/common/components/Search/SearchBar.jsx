import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, ButtonToolbar, Nav, Navbar, NavItem, Form, FormControl } from 'react-bootstrap';
import { SearchEngineSearchParam } from '../../types/main'
import { MyTable } from '../MyTable'
import { constants as mainConstants, actions as mainActions } from '../../../redux/modules/main';

import { memo, useEffect, useMemo, useState } from "react";
import { useTable } from 'react-table';

function  search(searchnetstring, type, config) {
    console.log(searchnetstring);
    if (searchnetstring === "") {
	return;
    }
    console.log(type);
    //console.log(props);
    var param = new SearchEngineSearchParam();
    param.config = config;
    param.str = searchnetstring;
    param.searchtype = type;
    console.log(searchnetstring + " " + type + " " + searchnetstring.value);
    console.log(Object.keys(searchnetstring));
    //console.log(Object.keys(props));
    //console.log(Object.keys(searchnetstring.target) + " " + searchnetstring.type);
    //console.log("bbb" + searchnetstring.target.value + " " + type);
    const url = Client.geturl("/search");
    //const url = geturl("/" + param.webpath);
    console.log("xxxx " + url);
    const fetchData = async(url) => {
	try {
	    console.log("xxxxyyyy " + url);
	    const response = await fetch(url, {
		method: "POST",
		headers: { 'Accept': 'application/json;charset=utf-8', 'Content-Type': 'application/json', },
		body: JSON.stringify(param),
	    });
	    console.log("xxxxyyyyzzzz " + response);
	    const json = await response.json();
	    console.log(json);
	    return json;
	} catch (error) {
	    console.log("error", error);
	}
    };
    const promise = fetchData(url);
    //console.log(result);
    promise.then(data => console.log(data));
    /*
   const data3 = React.useMemo(
	() => [], [] );
*/
    promise.then(data => {
   const data4 = React.useMemo(
	() => [], [] );
	const list = data.list;
	const tables = [];
	//for(var i = 0; i < list.length; i++) {
	const i = 0;
	const resultitemtable = list[i];
	const baseurl = Client.geturl("/");
	const mycolumns = MyTable.getcolumns(resultitemtable, baseurl);
	    const mydata = MyTable.getdata(resultitemtable);
	    const hcolumns = useMemo( () => mycolumns, [] );
	    const hdata = useMemo( () => mydata, [] );
	    const {
		getTableProps,
		getTableBodyProps,
		headerGroups,
		rows,
		prepareRow,
	    } = useTable({ columns: hcolumns, hdata });
	    const table = MyTable.gethtable(getTableProps, getTableBodyProps, headerGroups, rows, prepareRow);
	    tables.push(table);
            console.log(table);
	//}
	const htm = (
	    <div>
    		{ tables.map(item => item) }
	    </div>
	);
	mainActions.newtabMain(htm);
    });
    //promise.then(data => mainActions.newtabMain( MyTable.getTab(data.list, Date.now(), data)));
    //const tab = MyTable.getTab(result.list, Date.now(), result);
    //mainActions.newtabMain(tab);
}	      
	      

function     searchold(event, type) {
	var param = new SearchEngineSearchParam();
	param.config = this.props.config;
	param.str = event;
	param.searchtype = type;
	console.log(event + " " + type + " " + event.value);
	console.log(Object.keys(event));
	console.log(Object.keys(this.props));
	//console.log(Object.keys(event.target) + " " + event.type);
	//console.log("bbb" + event.target.value + " " + type);
    console.log("xxxx");
    useEffect((param) => {
    console.log("xxxxyyyy");
    const url = Client.geturl("/" + param.webpath);
    const fetchData = async(url) => {
    try {
        const response = await fetch(url, {
            method: "POST",
            headers: { 'Accept': 'application/json;charset=utf-8', 'Content-Type': 'application/json', },
            body: JSON.stringify(param),
        });
        const json = await response.json();
        console.log(json.slip.advice);
        //setAdvice(json.slip.advice);
	const bla = MyTable.t("hei");

    } catch (error) {
        console.log("error", error);
    }
    };

    fetchData(url);
}, []);
//	this.props.search(param.config, param, this.props)
  }
  


function SearchBar({ text, type, config, parentCallback, parentCallback2 }) {
    console.log(text);
    console.log(type);
    //console.log(config);
    const [ searchstring, setSearchstring ] = useState("");
    const [ searchnetstring, setSearchnetstring ] = useState("");
    //const [ result, setResult ] = useState({ list : [[]]});
    //console.log("bbb"+Object.keys(config));
    //console.log("bbb"+typeof text);
    console.log("searchnetstring" + searchnetstring);
    useEffect(() => {
	console.log("effect");
	console.log(searchnetstring);
	if (searchnetstring === "") {
	    return;
	}
	console.log(type);
	//console.log(props);
	var param = new SearchEngineSearchParam();
	param.config = config;
	param.str = searchnetstring;
	param.searchtype = type;
	console.log(searchnetstring + " " + type + " " + searchnetstring.value);
	console.log(Object.keys(searchnetstring));
	//console.log(Object.keys(props));
	//console.log(Object.keys(searchnetstring.target) + " " + searchnetstring.type);
	//console.log("bbb" + searchnetstring.target.value + " " + type);
	const url = Client.geturl("/search");
	//const url = geturl("/" + param.webpath);
	console.log("xxxx " + url);
	const bla = Client.fetchApi.search("/search", param);
	console.log(bla);
	
	const fetchData = async(url) => {
	    try {
		console.log("xxxxyyyy " + url);
		const response = await fetch(url, {
		    method: "POST",
		    headers: { 'Accept': 'application/json;charset=utf-8', 'Content-Type': 'application/json', },
		    body: JSON.stringify(param),
		});
		console.log("xxxxyyyyzzzz " + response);
		const result = await response.json();
		console.log(result);
		const tables = MyTable.getTabNew(result.list, Date.now(), parentCallback2);
		parentCallback(tables);
		//setResult(json);
	    } catch (error) {
		console.log("error", error);
	    }
	};
	fetchData(url).catch(console.error);
    }, [searchnetstring]);
    //const list = result.list;
    //console.log(result);
    //console.log(list);
    const tables = [];
    //for(var i = 0; i < list.length; i++) {
    /*
    const i = 0;
    const resultitemtable = list[i];
    const baseurl = Client.geturl("/");
    const mycolumns = MyTable.getcolumns(resultitemtable, baseurl, parentCallback2);
    console.log("calll");
    console.log(resultitemtable);
    const mydata = MyTable.getdata(resultitemtable);
    console.log(mycolumns);
    console.log(mydata);
*/
    //const hcolumns = useMemo( () => mycolumns); //, [mycolumns] );
    //const hdata = useMemo( () => mydata);//, [mydata] );
    if (searchnetstring !== undefined && searchnetstring.length > 0) {
	console.log("call"+searchnetstring);
	//console.log(hcolumns);
	//console.log(hdata);
	//parentCallback(mycolumns, mydata);
    }
    /*
    const {
	getTableProps,
	getTableBodyProps,
	headerGroups,
	rows,
	prepareRow,
    } = useTable({ columns: hcolumns, data: hdata });
    const table = MyTable.gethtable(getTableProps, getTableBodyProps, headerGroups, rows, prepareRow);
    tables.push(table);
    console.log(table);
*/
    //}
    const htm = (
	<div>
    	    { tables.map(item => item) }
	</div>
    );
    mainActions.newtabMain(htm);
    //search(searchnetstring, type, config);
    console.log("effectend");
    return (
	<div>
	    <Navbar>
		<Navbar.Header>
		    <Navbar.Brand>
			<a href="#home">{text}</a>
		    </Navbar.Brand>
		</Navbar.Header>
		<Nav>
		    <NavItem eventKey={3} href="#">
			<Form onSubmit={ (e) => setSearchnetstring(searchstring) }>
			    <FormControl
				placeholder="Enter text"
				onChange={ (e) => setSearchstring(e.target.value) }
				type="text"/>
			</Form>
		    </NavItem>
		</Nav>
            </Navbar>
	</div>
    );
}

export default memo(SearchBar);
