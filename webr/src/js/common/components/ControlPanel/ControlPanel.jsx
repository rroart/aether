import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, Button, ButtonToolbar, Nav, Navbar, NavItem, Form, FormControl } from 'react-bootstrap';
import { ServiceParam } from '../../types/main'
import DatePicker from 'react-datepicker';

import { memo, useCallback, useEffect, useMemo, useState } from "react";
import { useTable } from 'react-table';
import ReactTooltip from "react-tooltip";
import { MyTable } from '../MyTable'
import { Table } from '../Table'
import TaskList from './TaskList';

function ControlPanel ({ props, callbackNewTab }) {
  const [ indexnew, setIndexnew ] = useState('');
  const [ indexmd5, setIndexmd5 ] = useState('');
  const [ fsnew, setFsnew ] = useState('');
  const [ nonindex, setNonindex ] = useState('');
  const [ suffixindex, setSuffixindex ] = useState('');
  const [ startdate, setStartdate ] = useState('');
  const [ enddate, setEnddate ] = useState('');
  const [ reindex, setReindex ] = useState('');
  const [ cleanpath, setCleanpath ] = useState('');
  const [ deletepath, setDeletepath ] = useState('');
  const [ databasemd5, setDatabasemd5 ] = useState('');
  const [ databasesearch, setDatabasesearch ] = useState('');
  const [ param, setParam ] = useState(null);
  const [ hcolumns, setHcolumns ] = useState(null);
  const [ hdata, setHdata ] = useState(null);
  const [ uuids, setUuids ] = useState( new Set() );

  function filesystemlucenenew(path, md5checknew, props) {
    console.log(path);
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "FILESYSTEMLUCENENEW";
    param.add = path;
    param.md5checknew = md5checknew;
    param.webpath = "filesystemlucenenew";
    setParam(param);
  }

  function traverse(path, props) {
    console.log(path);
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "FILESYSTEM";
    param.add = path;
    param.webpath = "traverse";
    param.async = true;
    setParam(param);
  }

  function index(add, reindex, props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "INDEX";
    param.add = add;
    param.reindex = reindex;
    param.webpath = "index";
    param.async = true;
    setParam(param);
  }

  function indexsuffix(suffix, reindex, props) {
    var param = new ServiceParam();
    param.config = props.config
    param.function = "REINDEXSUFFIX";
    param.suffix = suffix;
    param.reindex = reindex;
    param.webpath = "indexsuffix";
    setParam(param);
  }

  function reindexdatelower(date, reindex, props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "REINDEXDATE";
    param.lowerdate = date;
    param.reindex = reindex;
    param.webpath = "reindexdatelower";
    setParam(param);
  }

  function reindexdatehigher(date,  reindex, props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "REINDEXDATE";
    param.higherdate = date;
    param.reindex = reindex;
    param.webpath = "reindexdatehigher";
    setParam(param);
  }
  function reindexlanguage(lang, props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "REINDEXLANGUAGE";
    param.lang = lang;
    param.webpath = "reindexlanguage";
    setParam(param);
  }

  function cleanupfs(dirname, props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "CONSISTENTCLEAN";
    param.dirname = dirname;
    param.webpath = "consistentclean";
    setParam(param);
  }

  function memoryusage(props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "MEMORYUSAGE";
    param.webpath = "memoryusage";
    setParam(param);
  }

  function notindexed(props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "NOTINDEXED";
    param.webpath = "notindexed";
    setParam(param);
  }

    function consistentclean( clean, path, props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "CONSISTENTCLEAN";
      param.clean = clean;
      param.path = path;
    param.webpath = "consistentclean";
    setParam(param);
    return;
  }

    function dbcheck( props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "DBCHECK";
    param.webpath = "dbcheck";
    setParam(param);
    return;
  }

  function deletepathdb( path, props) {
    var param = new ServiceParam();
    param.config = props.config;
    //param.function = Function.DELETEPATHDB;
    param.path = path;
    param.webpath = "deletepathdb";
    setParam(param);
  }

  function dbindex( md5, props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "DBINDEX";
    param.md5 = md5;
    param.webpath = "dbindex";
    setParam(param);
  }

  function dbsearch( md5, props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "DBSEARCH";
    param.md5 = md5;
    param.webpath = "dbsearch";
    setParam(param);
  }

  const { main } = props;
  useEffect(() => {
    if (param === undefined || param == null) {
      return;
    }
    const result = Client.fetchApi.search("/" + param.webpath, param);
    result.then(function(result) {
      const list = result.list;
      console.log(result);
      console.log(list);
      const baseurl = Client.geturl("/");
      if (param.async === true) {
        callbackAsync(result.uuid);
      } else {
        const tables = MyTable.getTabNew(result.list, Date.now(), callbackNewTab, props);
        callbackNewTab(tables);
      }
    });
  }, [param]);

  useEffect(() => {
    const timer = setInterval(getTask, 60000);
    return () => clearInterval(timer);
  }, []);

  const callbackAsync = useCallback( (uuid) => {
    uuids.push(uuid);
    setUuids([...uuids]);
  }, [uuids]);

  const getTask = async () => {
    for (let id in uuids) {
    const url = Client.geturl("/task/" + id);
    const settings = {
      method: 'GET',
    };
    const res = await fetch(url, settings);
    const data = await res.json();
    if (data != null) {
      const tables = MyTable.getTabNew(data.list, Date.now(), callbackNewTab, props);
      callbackNewTab(tables);
      uuids.delete(id);
    }
    setUuids([...uuids]);

    }
  };


  const languages = main && main.languages ? main.languages : null;
  //this.bardvd = new SearchBar('dvd');
  console.log(main);
  console.log(languages);
  console.log(Object.keys(main))
    console.log(main.config);
    //if (true) return (<div/>);
  return (
    <div>
      <h2>Hei</h2>
      <TaskList/>
      <Navbar>
          <Navbar.Brand>
            <a href="#home">Indexing new</a>
          </Navbar.Brand>
         <Nav>
          <NavItem eventKey={1} href="#">
            <Button bsStyle="primary" onClick={ (e) => filesystemlucenenew(null, false, props) }>Index filesystem new items</Button>
          </NavItem>
          <NavItem eventKey={2} href="#">
            <Form onSubmit={ (e) => filesystemlucenenew(indexnew, false, props)}>
              Index filesystem new items
              <FormControl
                onChange = { (e) => setIndexnew(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
          <NavItem eventKey={3} href="#">
            <Form onSubmit={ (e) => filesystemlucenenew(indexmd5, false, props)}>
              Filesystem index on changed md5 path
              <FormControl
                onChange = { (e) => setIndexmd5(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
           <Navbar.Brand>
            <a href="#home">Filesystem add new</a>
          </Navbar.Brand>
        <Nav>
          <NavItem eventKey={1} href="#">
            <Button bsStyle="primary" onClick={ (e) => traverse(null, props) }>Filesystem add new</Button>
          </NavItem>
          <NavItem eventKey={2} href="#">
            <Form onSubmit={ (e) => traverse(fsnew, props)}>
              Filesystem add new
              <FormControl
                onChange = { (e) => setFsnew(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
           <Navbar.Brand>
            <a href="#home">Indexed non-indexed</a>
          </Navbar.Brand>
         <Nav>
          <NavItem eventKey={1} href="#">
            <Button bsStyle="primary" onClick={ (e) => index(null, false, props) }>Index non-indexed items</Button>
          </NavItem>
          <NavItem eventKey={2} href="#">
            <Form onSubmit={ (e) => index(nonindex, false, props)}>
              Index non-indexed items
              <FormControl
                onChange = { (e) => setNonindex(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
        <Nav>
          <NavItem eventKey={1} href="#">
            <Form onSubmit={ (e) => indexsuffix(suffixindex, false, props)}>
              Index on suffix
              <FormControl
                onChange = { (e) => setSuffixindex(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
          <NavItem eventKey={2} href="#">
            <Form onSubmit={ (e) => indexsuffix(suffixindex, true, props)}>
              Reindex on suffix
              <FormControl
                onChange = { (e) => setSuffixindex(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
        <Nav>
          <NavItem eventKey={2} href="#">
            Reindex on before date
            <DatePicker id="startdatepicker" onChange={e => reindexdatelower(e, null, props)}/>
          </NavItem>
          <NavItem eventKey={3} href="#">
            Reindex on after date
            <DatePicker id="enddatepicker" onChange={e => reindexdatehigher(e, null, props)}/>
          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
        <Nav>
          <NavItem eventKey={1} href="#">
            <Form onSubmit={ (e) => index(reindex, true, props)}>
              Reindex
              <FormControl
                onChange = { (e) => setReindex(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
          <NavItem eventKey={2} href="#">
            Reindex language
            <Select options="[{size:'5'}]"
                    onChange={e => reindexlanguage(e, props)}
                    options={languages}
            />

          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
        <Nav>
          <NavItem eventKey={1} href="#">
            <Form onSubmit={ (e) => deletepathdb(deletepath, props)}>
              Delete path from db
              <FormControl
                onChange = { (e) => setDeletepath(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
          <Navbar.Brand>
            <a href="#home">Consistency</a>
          </Navbar.Brand>
         <Nav>
          <NavItem eventKey={1} href="#">
              <Button bsStyle="primary" onClick={ (e) => consistentclean(false, cleanpath, props) }>Get consistency</Button>
          </NavItem>
          <NavItem eventKey={2} href="#">
              <Button bsStyle="primary" onClick={ (e) => consistentclean(true, cleanpath, props) }>Get consistency and clean</Button>
          </NavItem>
	    <Form>
              <FormControl
                onChange = { (e) => setCleanpath(e.target.value) }
                type="text"/>
            </Form>
        </Nav>
      </Navbar>
      <Navbar>
           <Navbar.Brand>
            <a href="#home">Db check</a>
          </Navbar.Brand>
         <Nav>
          <NavItem eventKey={1} href="#">
              <Button bsStyle="primary" onClick={ (e) => dbcheck(props) }>Db check</Button>
          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
         <Navbar.Brand>
            <a href="#home"></a>
          </Navbar.Brand>
        <Nav>
          <NavItem eventKey={1} href="#">
            <Button bsStyle="primary" onClick={ (e) => notindexed(props) }>Get not yet indexed</Button>
          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
        <Nav>
          <NavItem eventKey={1} href="#">
            <Button bsStyle="primary" onClick={ (e) => memoryusage(props) }>Memory usage</Button>
          </NavItem>
        </Nav>
      </Navbar>
      <Navbar>
        <Nav>
          <NavItem eventKey={1} href="#">
            <Form onSubmit={ (e) => dbindex(databasemd5, props)}>
              Database md5 id
              <FormControl
                onChange = { (e) => setDatabasemd5(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
          <NavItem eventKey={2} href="#">
            <Form onSubmit={ (e) => dbsesearch(databasesearch, true, props)}>
              Database search
              <FormControl
                onChange = { (e) => setDatabasesearch(e.target.value) }
                type="text"/>
            </Form>
          </NavItem>
        </Nav>
      </Navbar>
    </div>
  );
}

export default memo(ControlPanel);
