import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, ButtonToolbar, Nav, Navbar, NavItem, FormControl } from 'react-bootstrap';

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

class SearchBar extends PureComponent {
    type : string;
    text: string;
    constructor(props) {
    super(props);
      this.type = props.type;
      this.text = props.text;
	console.log("bbb"+Object.keys(props));
	console.log("bbb"+typeof props.text);
}

handleYearChange = (e) => {
  console.log(e);
  const value = e.value;
  var result;
}

    handleChange(event, type) {
	console.log(event + " " + type + " " + event.value);
	console.log(Object.keys(event));
	console.log(Object.keys(event.target) + " " + event.type);
	console.log("bbb" + event.target.value + " " + type);
	props.search(event.target.value, type, props)
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
          <FormControl
            type="text"
            placeholder="Enter text"
              onChange={ (e) => this.handleChange(e, this.type, this.props) }
          />
        </NavItem>
            </Nav>
          </Navbar>
      </div>
    );
  }
}

export default SearchBar;
