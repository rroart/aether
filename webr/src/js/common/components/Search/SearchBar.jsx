import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, ButtonToolbar, Nav, Navbar, NavItem, Form, FormControl } from 'react-bootstrap';
import { SearchEngineSearchParam } from '../../types/main'

class SearchBar extends PureComponent {
    type : string;
    text: string;
    searchstring: string;
    constructor(props) {
    super(props);
      this.type = props.type;
      this.text = props.text;
	console.log("bbb"+Object.keys(props));
	console.log("bbb"+typeof props.text);
}

    search(event, type) {
	var param = new SearchEngineSearchParam();
	param.config = this.props.config;
	param.str = event;
	param.searchtype = type;
	console.log(event + " " + type + " " + event.value);
	console.log(Object.keys(event));
	console.log(Object.keys(this.props));
	//console.log(Object.keys(event.target) + " " + event.type);
	//console.log("bbb" + event.target.value + " " + type);
	this.props.search(param.config, param, this.props)
  }
  
  render() {
    return (
      <div>
      <Navbar>
        <Navbar.Header>
          <Navbar.Brand>
            <a href="#home">{this.text}</a>
          </Navbar.Brand>
        </Navbar.Header>
        <Nav>
            <NavItem eventKey={3} href="#">
		<Form onSubmit={ (e) => this.search(this.searchstring, this.type, this.props) }>
          <FormControl
            placeholder="Enter text"
              onChange={ (e) => this.searchstring = e.target.value }
            type="text"/>
		</Form>
        </NavItem>
            </Nav>
          </Navbar>
      </div>
    );
  }
}

export default SearchBar;
