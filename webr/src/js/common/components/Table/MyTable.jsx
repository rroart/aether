/* eslint-disable no-undef */
import React, { PureComponent } from 'react';
import ReactTable from "react-table";
import 'react-table/react-table.css';
import ReactTooltip from "react-tooltip";

function handleButtonClick(props, e, value) {
    console.log("hhaha");
    console.log(e);
    console.log(value);
    console.log(props);
    props.getcontentgraph(props.main.config, value, props);
}

function convert(resultitemtable, date, props) {
    console.log("here");
    console.log(resultitemtable);
    const array = resultitemtable;
    console.log(array);
    if (array.length == 0) {
	return (
	    <ReactTable key={date} data={ [] } columns={ [] } />
	);
    }
    const head = array[0];
    const rest = array.slice(1);
    const columns = [];
    const result = [];
    console.log(head);
    console.log(head.items);
    console.log(head.items.length);
    for(var i = 0; i < head.items.length; i++) {
	if (head.items[i] != "Score") {
	    //columns.push({ accessor: head[i], Header: head[i], sort: true, id: 'button', Cell: ({value}) => (<a onClick={console.log('clicked value', value)}>Button</a>) });
	    //columns.push({ accessor: head.items[i], Header: head.items[i], sort: true, id: 'button'+i, Cell: (row) => (<div><span title={row.value}>{row.value}</span></div>) });
	    columns.push({ accessor: head.items[i], Header: head.items[i], sort: true, id: 'col'+i, Cell: (row) => (<span data-tip = {row.value}>{row.value}</span>) });
	    //columns.push({ accessor: head.items[i], Header: head.items[i], sort: true, id: 'button'+i, Cell: (row) => (<span id={head.items[i]} data-tip = {row.value} dangerouslySetInnerHTML={{__html: row.value }}/>) });
	} else {
	    columns.push({ accessor: head.items[i], Header: head.items[i], sort: true });
	}
    }
    console.log(columns);
    console.log(head);
    console.log(Object.keys(head));
    console.log(head.length);
    console.log(rest);
    console.log(rest.length);
    for(var j = 0; j < rest.length; j++) {
	const row = rest[j].items;
	console.log(row);
	const newrow = [];
	for(var i = 0; i < head.items.length; i++) {
	    console.log(i);
	    newrow[head.items[i]] = row[i];
	    console.log(i);
	}
	/*
	if (head[0] == "Img") {
	    newrow[head[0]] = "bla";
	}
	*/
	result.push(newrow);
    }
    console.log(result);
    console.log(columns);
    return (
	<div>
	    <ReactTooltip effect="solid" html="true"/>
		<ReactTable key={date} data={ result } columns={ columns } onPageSizeChange={() => { ReactTooltip.rebuild();}}/>
		</div>
  );
}

function convert2(array) {
    console.log("here");
    console.log(array);
    const head = array[0];
    const rest = array.slice(1);
    const columns = [];
    const result = [];
    for(var i = 0; i < head.length; i++) {
	columns.push({ accessor: head[i], Header: head[i]});
    }
    console.log(columns);
    console.log(head);
    console.log(head.length);
    console.log(rest);
    console.log(rest.length);
    for(var j = 0; j < rest.length; j++) {
	const row = rest[j];
	const newrow = [];
	for(var i = 0; i < head.length; i++) {
	    newrow[head[i]] = row[i];
	}
	result.push(newrow);
    }
    console.log(result);
    console.log(columns);
    return (
    <ReactTable data={ result } columns={ columns } />
  );
}

function convertbs(resultitemtable) {
    console.log("here");
    const array = resultitemtable.rows;
    console.log(array);
    const head = array[0];
    const rest = array.slice(1);
    const columns = [];
    const result = [];
    for(var i = 0; i < head.cols.length; i++) {
	columns.push({ dataField: head.cols[i], text: head.cols[i], sort: true });
    }
    console.log(columns);
    console.log(head);
    console.log(head.length);
    console.log(rest);
    console.log(rest.length);
    for(var j = 0; j < rest.length; j++) {
	const row = rest[j];
	//console.log(row);
	const newrow = [];
	for(var i = 0; i < head.cols.length; i++) {
	    newrow[head.cols[i]] = row.cols[i];
	}
	result.push(newrow);
    }
    console.log(result);
    return (
    <BootstrapTable keyField='id' data={ result } columns={ columns } />
  );
}

function convert2bs(array) {
    console.log("here");
    console.log(array);
    const head = array[0];
    const rest = array.slice(1);
    const columns = [];
    const result = [];
    for(var i = 0; i < head.length; i++) {
	columns.push({ dataField: head[i], text: head[i]});
    }
    console.log(columns);
    console.log(head);
    console.log(head.length);
    console.log(rest);
    console.log(rest.length);
    for(var j = 0; j < rest.length; j++) {
	const row = rest[j];
	const newrow = [];
	for(var i = 0; i < head.length; i++) {
	    newrow[head[i]] = row[i];
	}
	result.push(newrow);
    }
    console.log(result);
    return (
    <BootstrapTable keyField='id' data={ result } columns={ columns } />
  );
}

function getTable(resultitemtable, date, props) {
    return convert(resultitemtable, date, props);
}

function image(data, i) {
    const Example = ({ data }) => <img src={`data:image/svg+xml;base64,${data}`} width="1000" height="800"/>;
    return (
	<Example key={i} data={data} />
    );
}

function getTab(list, date, props) {
    const tables = [];
    for(var i = 0; i < list.length; i++) {
	console.log(list[i])
	console.log(list[i]._class)
	if (true || list[i]._class == "roart.model.ResultItemTable") {
	    const resultitemtable = list[i];
	    const table = getTable(resultitemtable, date + i, props);
	    tables.push(table);
	    console.log(table);
	}
	if (list[i]._class == "roart.model.ResultItemStream") {
	    const stream = list[i];
	    //let buff = new Buffer(stream.bytes, 'base64');
	    //let text = buff.toString('ascii');
	    //console.log(text);
	    const example = image(stream.bytes, i);
	    tables.push(example);
	    console.log(example);
	}
    }
    return(
    <div>
    	  { tables.map(item => item) }
	  </div>
    );
}

const MyTable = { convert, convert2, getTab };
export default MyTable;
