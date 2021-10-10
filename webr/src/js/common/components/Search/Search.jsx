import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, ButtonToolbar, Nav, Navbar, NavItem, FormControl } from 'react-bootstrap';
import SearchBar from './SearchBar';
const options = [
  { value: 'chocolate', label: 'Chocolate' },
  { value: 'strawberry', label: 'Strawberry' },
  { value: 'vanilla', label: 'Vanilla' }
];

const options2 = [
  { label: 'chocolate' },
  { label: 'strawberry' },
  { label: 'vanilla' }
];

class Search extends PureComponent {
  bardvd : SearchBar;
  constructor() {
    super();
}

  render() {
const { main } = this.props;
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
      if (!!main.config) {
	  const dolucene = main.config.get('configValueMap').get("searchengine.lucene[@enable]");
	  const dosolr = main.config.get('configValueMap').get("searchengine.solr[@enable]");
	  const doelastic = main.config.get('configValueMap').get("searchengine.elastic[@enable]");
	  if (dolucene) {
	      Searchbars=<div>
			     <SearchBar text='Search standard' type='0'/>
			     <SearchBar text='Search analyzing' type='1'/>
			     <SearchBar text='Search complexphrase' type='2'/>
			     <SearchBar text='Search extendable' type='3'/>
			     <SearchBar text='Search multi' type='4'/>
			     <SearchBar text='Search surround' type='5'/>
			     <SearchBar text='Search classic' type='6'/>
			     <SearchBar text='Search simple' type='7'/>
			 </div>;
	  }
	  if (dosolr) {
	      Searchbars=<div>
			     <SearchBar text='Search default' type='0'/>
			     <SearchBar text='Search lucene' type='1'/>
			     <SearchBar text='Search complexphrase' type='2'/>
			     <SearchBar text='Search surround' type='3'/>
			     <SearchBar text='Search simple' type='4'/>
			 </div>;
	  }
	  if (doelastic) {
	      Searchbars=<div>
			     <SearchBar text='Search' type='0'/>
			 </div>;
	  }
      }
      return (
	  <div>
	      { Searchbars }
	      </div>
    );
  }
/*
function search2(query, cb) {
return fetch(`http://localhost:8080/misc/cd/creator`, {
accept: 'application/json',
}).then(checkStatus)
.then(parseJSON)
.then(cb);
}
*/
}

export default Search;
