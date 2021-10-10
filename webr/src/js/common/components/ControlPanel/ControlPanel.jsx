import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, Button, ButtonToolbar, Nav, Navbar, NavItem, Form, FormControl } from 'react-bootstrap';
import type { ServiceParam } from '../../types/main'

class ControlPanel extends PureComponent {
  constructor() {
    super();
}

    filesystemlucenenew(event, md5checknew, props) {
    console.log(event);
    console.log(event.value);
    console.log(event.target);
    console.log(event.target.value);
    console.log(props);
	var param = new ServiceParam();
	param.config = this.props.config;
	param.function = "FILESYSTEMLUCENENEW";
	param.add = event.value;
	param.md5checknew = md5checknew;
	param.webpath = "filesystemlucenenew";
    //props.control([ param ]);
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
	      <h2>Hei</h2>
	      <Navbar>
		  <Navbar.Header>
		      <Navbar.Brand>
			  <a href="#home">Indexing</a>
		      </Navbar.Brand>
		  </Navbar.Header>
		  <Nav>
		      <NavItem eventKey={1} href="#">
			  <Button bsStyle="primary" onClick={ (e) => this.filesystemlucenenew(e, false, this.props) }>Index filesystem new items</Button>
		      </NavItem>
		      <NavItem eventKey={2} href="#">
			  <Form onSubmit={ (e) => this.filesystemlucenenew(e, false, this.props)}>
			      <FormControl
				  type="text"
				  placeholder="New items path"/>
			  </Form>
		      </NavItem>
		      <NavItem eventKey={3} href="#">
			  <FormControl
			      type="text"
			      placeholder="Changed md5 path"
			      onSubmit={ (e) => this.filesystemlucenenew(e, false, this.props) }
			  />
		      </NavItem>
		  </Nav>
              </Navbar>
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

export default ControlPanel;
