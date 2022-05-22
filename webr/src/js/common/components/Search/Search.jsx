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
    const [ hdata, setHdata ] = useState(null);
    
    console.log(props);
    const callback = useCallback((hcolumns, hdata) => {
	console.log("callback");
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

	console.log("call " + htm);
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
			     <SearchBar text='Search standard' type='0' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search analyzing' type='1' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search complexphrase' type='2' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search extendable' type='3' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search multi' type='4' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search surround' type='5' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search classic' type='6' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search simple' type='7' config={props.config} parentCallback={callback}/>
			 </div>;
	  }
	  if (dosolr) {
	      Searchbars=<div>
			     <SearchBar text='Search default' type='0' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search lucene' type='1' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search complexphrase' type='2' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search surround' type='3' config={props.config} parentCallback={callback}/>
			     <SearchBar text='Search simple' type='4' config={props.config} parentCallback={callback}/>
			 </div>;
	  }
	  if (doelastic) {
	      Searchbars=<div>
			     <SearchBar text='Search' type='0' config={props.config} parentCallback={callback}/>
			 </div>;
	  }
     // }
      return (
	  <div>
	      { Searchbars }
	       <ReactTooltip effect="solid" html="true"/>
	      <Table hcolumns={hcolumns} hdata={hdata} />
	      </div>
    );
}

export default memo(Search);
