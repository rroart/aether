import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, Button, ButtonToolbar, Nav, Navbar, NavItem, Form, FormControl } from 'react-bootstrap';
import type { ServiceParam } from '../../types/main'
import DatePicker from 'react-16-bootstrap-date-picker';

class ControlPanel extends PureComponent {
  constructor() {
      super();
      this.indexnew = '';
      this.indexmd5 = '';
      this.fsnew = '';
      this.nonindex = '';
      this.suffixindex = '';
      this.suffixindex = '';
      this.startdate = '';
      this.enddate = '';
      this.reindex = '';
      this.deletepath = '';
      this.databasemd5 = '';
      this.databasesearch = '';
}

    filesystemlucenenew(path, md5checknew, props) {
	console.log(path);
	var param = new ServiceParam();
	param.config = props.config;
	param.function = "FILESYSTEMLUCENENEW";
	param.add = path;
	param.md5checknew = md5checknew;
	param.webpath = "filesystemlucenenew";
    //props.control([ param ]);
  }

    traverse(path, props) {
	console.log(path);
	var param = new ServiceParam();
	param.config = props.config;
	param.function = "FILESYSTEM";
	param.add = path;
	param.webpath = "traverse";
	console.log("h");
	props.control(param.config, param, props);
	console.log("h2");
    //props.control([ param ]);
  }

    index(add, reindex, props) {
        var param = new ServiceParam();
            param.config = this.props.config;
        param.function = "INDEX";
        param.add = add;
        param.reindex = reindex;
        param.webpath = "index";
    }

    indexsuffix(suffix, reindex, props) {
        var param = new ServiceParam();
        param.config = this.props.config
        param.function = "REINDEXSUFFIX";
        param.suffix = suffix;
        param.reindex = reindex;
        param.webpath = "indexsuffix";
	}

        reindexdatelower(date, reindex) {
        var param = new ServiceParam();
        param.config = this.props.config;
        param.function = "REINDEXDATE";
        param.lowerdate = date;
        param.reindex = reindex;
        param.webpath = "reindexdatelower";
    }

 reindexdatehigher(date,  reindex) {
        var param = new ServiceParam();
        param.config = this.props.config;
        param.function = "REINDEXDATE";
        param.higherdate = date;
        param.reindex = reindex;
        param.webpath = "reindexdatehigher";
   }
 reindexlanguage(lang) {
        var param = new ServiceParam();
        param.config = this.props.config;
        param.function = "REINDEXLANGUAGE";
        param.lang = lang;
        param.webpath = "reindexlanguage";
    }

  cleanupfs(dirname) {
        var param = new ServiceParam();
        param.config = this.props.config;
        param.function = "CONSISTENTCLEAN";
        param.dirname = dirname;
        param.webpath = "cleanupfs";
    }

    memoryusage() {
        var param = new ServiceParam();
        param.config = this.props.config;
        param.function = "MEMORYUSAGE";
        param.webpath = "memoryusage";
    }

    notindexed() {
        var param = new ServiceParam();
        param.config = this.props.config;
        param.function = "NOTINDEXED";
        param.webpath = "notindexed";
    }

     consistentclean( clean) {
        var param = new ServiceParam();
        param.config = this.props.config;
        param.function = "CONSISTENTCLEAN";
	    param.clean = clean;
        param.webpath = "consistentclean";
        Queues.clientQueue.add(param);
        return;           
	    }

        deletepathdb( path) {
            var param = new ServiceParam();
            param.config = this.props.config;
            //param.function = Function.DELETEPATHDB;
            param.path = path;
            param.webpath = "deletepathdb";
       }

     dbindex( md5) {
        var param = new ServiceParam();
        param.config = this.props.config;
        param.function = "DBINDEX";
        param.md5 = md5;
        param.webpath = "dbindex";
    }

     dbsearch( md5)  {
        var param = new ServiceParam();
        param.config = this.props.config;
        param.function = "DBSEARCH";
        param.md5 = md5;
        param.webpath = "dbsearch";
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
			    <a href="#home">Indexing new</a>
			</Navbar.Brand>
		    </Navbar.Header>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Button bsStyle="primary" onClick={ (e) => this.filesystemlucenenew(null, false, this.props) }>Index filesystem new items</Button>
			</NavItem>
			<NavItem eventKey={2} href="#">
			    <Form onSubmit={ (e) => this.filesystemlucenenew(this.indexnew, false, this.props)}>
				Index filesystem new items
				<FormControl
				    onChange = { (e) => this.indexnew = e.target.value }
				    type="text"/>
			    </Form>
			</NavItem>
			<NavItem eventKey={3} href="#">
			    <Form onSubmit={ (e) => this.filesystemlucenenew(this.indexmd5, false, this.props)}>
				Filesystem index on changed md5 path
				<FormControl
				    onChange = { (e) => this.indexmd5 = e.target.value }
				    type="text"/>
			    </Form>
			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Navbar.Header>
			<Navbar.Brand>
			    <a href="#home">Filesystem add new</a>
			</Navbar.Brand>
		    </Navbar.Header>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Button bsStyle="primary" onClick={ (e) => this.traverse(null, this.props) }>Filesystem add new</Button>
			</NavItem>
			<NavItem eventKey={2} href="#">
			    <Form onSubmit={ (e) => this.traverse(this.fsnew, this.props)}>
				Filesystem add new
				<FormControl
				    onChange = { (e) => this.fsnew = e.target.value }
				    type="text"/>
			    </Form>
			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Navbar.Header>
			<Navbar.Brand>
			    <a href="#home">Indexed non-indexed</a>
			</Navbar.Brand>
		    </Navbar.Header>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Button bsStyle="primary" onClick={ (e) => this.index(null, false, this.props) }>Index non-indexed items</Button>
			</NavItem>
			<NavItem eventKey={2} href="#">
			    <Form onSubmit={ (e) => this.index(this.nonindex, false, this.props)}>
				Index non-indexed items
				<FormControl
				    onChange = { (e) => this.nonindex = e.target.value }
				    type="text"/>
			    </Form>
			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Form onSubmit={ (e) => this.indexsuffix(this.suffixindex, false, this.props)}>
				Index on suffix
				<FormControl
				    onChange = { (e) => this.suffixindex = e.target.value }
				    type="text"/>
			    </Form>
			</NavItem>
			<NavItem eventKey={2} href="#">
			    <Form onSubmit={ (e) => this.indexsuffix(this.suffixindex, true, this.props)}>
				Reindex on suffix
				<FormControl
				    onChange = { (e) => this.suffixindex = e.target.value }
				    type="text"/>
			    </Form>
			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Nav>
			<NavItem eventKey={2} href="#">
			    Reindex on before date
			    <DatePicker id="startdatepicker" onChange={e => this.reindexdatelower(e, null)}/>
			</NavItem>
			<NavItem eventKey={3} href="#">
			    Reindex on after date
			    <DatePicker id="enddatepicker" onChange={e => this.reindexdatehigher(e, null)}/>
			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Form onSubmit={ (e) => this.index(this.reindex, true, this.props)}>
				Reindex
				<FormControl
				    onChange = { (e) => this.reindex = e.target.value }
				    type="text"/>
			    </Form>
			</NavItem>
			<NavItem eventKey={2} href="#">
			    Reindex language
			    <Select options="[{size:'5'}]"
				    onChange={e => this.reindexlanguage(e, this.props)}
				    options={this.props.languages}
			    />

			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Form onSubmit={ (e) => this.deletepathdb(this.deletepath, this.props)}>
				Delete path from db
				<FormControl
				    onChange = { (e) => this.deletepath = e.target.value }
				    type="text"/>
			    </Form>
			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Navbar.Header>
			<Navbar.Brand>
			    <a href="#home">Consistency</a>
			</Navbar.Brand>
		    </Navbar.Header>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Button bsStyle="primary" onClick={ (e) => this.consistentclean(false, this.props) }>Get consistency</Button>
			</NavItem>
			<NavItem eventKey={2} href="#">
			    <Button bsStyle="primary" onClick={ (e) => this.consistentclean(true, this.props) }>Get consistency and clean</Button>
			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Navbar.Header>
			<Navbar.Brand>
			    <a href="#home"></a>
			</Navbar.Brand>
		    </Navbar.Header>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Button bsStyle="primary" onClick={ (e) => this.notindexed(this.props) }>Get not yet indexed</Button>
			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Button bsStyle="primary" onClick={ (e) => this.memoryusage(this.props) }>Memory usage</Button>
			</NavItem>
		    </Nav>
		</Navbar>
		<Navbar>
		    <Nav>
			<NavItem eventKey={1} href="#">
			    <Form onSubmit={ (e) => this.databasemd5(this.databasemd5, false, this.props)}>
				Database md5 id
				<FormControl
				    onChange = { (e) => this.databasemd5 = e.target.value }
				    type="text"/>
			    </Form>
			</NavItem>
			<NavItem eventKey={2} href="#">
			    <Form onSubmit={ (e) => this.databasesearch(this.databasesearch, true, this.props)}>
				Database search
				<FormControl
				    onChange = { (e) => this.databasesearch = e.target.value }
				    type="text"/>
			    </Form>
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
