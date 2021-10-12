import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, ButtonToolbar, Nav, Navbar, NavItem, FormControl } from 'react-bootstrap';
import SearchBar from './SearchBar';

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
			     <SearchBar text='Search standard' type='0' {...this.props}/>
			     <SearchBar text='Search analyzing' type='1' {...this.props}/>
			     <SearchBar text='Search complexphrase' type='2' {...this.props}/>
			     <SearchBar text='Search extendable' type='3' {...this.props}/>
			     <SearchBar text='Search multi' type='4' {...this.props}/>
			     <SearchBar text='Search surround' type='5' {...this.props}/>
			     <SearchBar text='Search classic' type='6' {...this.props}/>
			     <SearchBar text='Search simple' type='7' {...this.props}/>
			 </div>;
	  }
	  if (dosolr) {
	      Searchbars=<div>
			     <SearchBar text='Search default' type='0' {...this.props}/>
			     <SearchBar text='Search lucene' type='1' {...this.props}/>
			     <SearchBar text='Search complexphrase' type='2' {...this.props}/>
			     <SearchBar text='Search surround' type='3' {...this.props}/>
			     <SearchBar text='Search simple' type='4' {...this.props}/>
			 </div>;
	  }
	  if (doelastic) {
	      Searchbars=<div>
			     <SearchBar text='Search' type='0' {...this.props}/>
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
