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
      console.log(Object.keys(main))
      console.log(main.config);
      //console.log(main.config.get("searchengine.lucene"));
      //console.log(main.config.get("searchengine.lucene[@enable]"));
      return (
      <div>
	  <SearchBar text='Search standard' type='0'/>
	  <SearchBar text='Search analyzing' type='1'/>
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
