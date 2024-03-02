import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { DropdownButton, MenuItem, Button, ButtonToolbar, Nav, Navbar, NavItem, Form, FormControl } from 'react-bootstrap';
import { ServiceParam } from '../../types/main'
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";

import { memo, useCallback, useEffect, useMemo, useState } from "react";
import { useTable } from 'react-table';
import ReactTooltip from "react-tooltip";
import { MyTable } from '../MyTable'
import { Table } from '../Table'
import TaskList from './TaskList';

function ControlPanel ({ props, callbackNewTab }) {
  const [ startdate, setStartdate ] = useState(null);
  const [ enddate, setEnddate ] = useState(null);
  const [ param, setParam ] = useState(null);
  const [ hcolumns, setHcolumns ] = useState(null);
  const [ hdata, setHdata ] = useState(null);
  const [ uuids, setUuids ] = useState( new Set() );
  const [ suffix, setSuffix ] = useState( null );
  const [ path, setPath ] = useState( null );
  const [ language, setLanguage ] = useState( null );
  const [ search, setSearch ] = useState( null );

  function filesystemlucenenew(md5checknew, props) {
    console.log(path);
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "FILESYSTEMLUCENENEW";
    param.path = path;
    param.md5checknew = md5checknew;
    param.suffix = suffix;
    param.webpath = "task";
    setParam(param);
  }

  function traverse(props) {
    console.log(path);
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "FILESYSTEM";
    param.path = path;
    param.suffix = suffix;
    param.webpath = "task";
    param.async = true;
    setParam(param);
  }

  function index(props, reindex) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "INDEX";
    param.path = path;
    param.suffix = suffix;
    param.lang = language;
    param.reindex = reindex;
    param.lowerdate = startdate;
    param.higherdate = enddate;
    param.webpath = "task";
    param.async = true;
    setParam(param);
  }

  function memoryusage(props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "MEMORYUSAGE";
    param.webpath = "task";
    setParam(param);
  }

  function notindexed(props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "NOTINDEXED";
    param.webpath = "task";
    setParam(param);
  }

    function consistentclean(clean, props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "CONSISTENTCLEAN";
      param.clean = clean;
      param.path = path;
    param.webpath = "task";
    setParam(param);
    return;
  }

    function dbcheck( props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "DBCHECK";
    param.webpath = "task";
    setParam(param);
    return;
  }

  function deletepathdb(props) {
    var param = new ServiceParam();
    param.config = props.config;
    //param.function = Function.DELETEPATHDB;
    param.function = "DELETEPATH";
    param.path = path;
    param.webpath = "task";
    setParam(param);
  }

  function dbindex( props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "DBINDEX";
    param.search = search;
    param.webpath = "task";
    setParam(param);
  }

  function dbsearch( props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "DBSEARCH";
    param.path = path;
    param.suffix = suffix;
    param.lang = language;
    param.lowerdate = startdate;
    param.higherdate = enddate;
    param.search = search;
    param.webpath = "task";
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
    uuids.add(uuid);
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
    var useStartdate = new Date();
    var useEnddate = new Date();
    if (startdate != null) {
	useStartdate = startdate;
    }
    if (enddate != null) {
	useEnddate = enddate;
    }
    //if (true) return (<div/>);
  return (
    <div>
      <h2>Hei</h2>
      <TaskList/>
      <Navbar>
        <Nav>
          Before date
            <DatePicker id="startdatepicker" selected={useStartdate} onChange={e => setStartdate(e)}/>
          After date
            <DatePicker id="enddatepicker" selected={useEnddate} onChange={e => setEnddate(e)}/>
        </Nav>
      </Navbar>
      <Nav>
        <Form>
          Path
          <FormControl
            onChange = { (e) => setPath(e.target.value) }
            type="text"/>
        </Form>
      </Nav>
      <Nav>
        <Form>
           Suffix
          <FormControl
            onChange = { (e) => setSuffix(e.target.value) }
            type="text"/>
        </Form>
      </Nav>
      <Nav>
        <Form>
          Search
          <FormControl
            onChange = { (e) => setSearch(e.target.value) }
            type="text"/>
        </Form>
      </Nav>
      <Nav>
        <Select options="[{size:'5'}]"
                onChange={e => setLanguage(e)}
                options={languages}
        />
      </Nav>
        <Navbar>
          <Navbar.Brand>
            Indexing new
          </Navbar.Brand>
         <Nav>
            <Button bsStyle="primary" onClick={ (e) => filesystemlucenenew(false, props) }>Index filesystem new items</Button>
        </Nav>
      </Navbar>
      <Navbar>
           <Navbar.Brand>
            Filesystem add new
          </Navbar.Brand>
        <Nav>
            <Button bsStyle="primary" onClick={ (e) => traverse(props) }>Filesystem add new</Button>
         </Nav>
      </Navbar>
      <Navbar>
           <Navbar.Brand>
            Indexed non-indexed
          </Navbar.Brand>
         <Nav>
            <Button bsStyle="primary" onClick={ (e) => index(false, props) }>Index non-indexed items</Button>
        </Nav>
      </Navbar>
       <Navbar>
        <Nav>
          <Button bsStyle="primary" onClick={ (e) => index(props, false) }>Index</Button>
          <Button bsStyle="primary" onClick={ (e) => index(props, true) }>Reindex</Button>
        </Nav>
      </Navbar>
       <Navbar>
        <Nav>
          <Button bsStyle="primary" onClick={ (e) => deletepathdb(props) }>Get consistency</Button>
        </Nav>
         </Navbar>
      <Navbar>
          <Navbar.Brand>
            Consistency
          </Navbar.Brand>
         <Nav>
              <Button bsStyle="primary" onClick={ (e) => consistentclean(false, props) }>Get consistency</Button>
              <Button bsStyle="primary" onClick={ (e) => consistentclean(true, props) }>Get consistency and clean</Button>
        </Nav>
      </Navbar>
      <Navbar>
           <Navbar.Brand>
            Db check
          </Navbar.Brand>
         <Nav>
              <Button bsStyle="primary" onClick={ (e) => dbcheck(props) }>Db check</Button>
        </Nav>
      </Navbar>
      <Navbar>
         <Navbar.Brand>
            Get not yet indexed
          </Navbar.Brand>
        <Nav>
            <Button bsStyle="primary" onClick={ (e) => notindexed(props) }>Get not yet indexed</Button>
        </Nav>
      </Navbar>
      <Navbar>
        <Nav>
            <Button bsStyle="primary" onClick={ (e) => memoryusage(props) }>Memory usage</Button>
        </Nav>
      </Navbar>
      <Navbar>
        <Nav>
          <Button bsStyle="primary" onClick={ (e) => dbindex(props) }>Database search index</Button>
          <Button bsStyle="primary" onClick={ (e) => dbsearch(props) }>Database search</Button>
        </Nav>
      </Navbar>
    </div>
  );
}

export default memo(ControlPanel);
