import React, { PureComponent } from 'react';

import { Client, ConvertToSelect } from '../util'
import Select from 'react-select';
import { Button, Nav, Navbar, Form, FormControl } from 'react-bootstrap';
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
  const [ failedlimit, setFailedlimit ] = useState(null);
  const [ param, setParam ] = useState(null);
  const [ hcolumns, setHcolumns ] = useState(null);
  const [ hdata, setHdata ] = useState(null);
  const [ uuids, setUuids ] = useState( new Set<string>() );
  const [ suffix, setSuffix ] = useState( null );
  const [ path, setPath ] = useState( null );
  const [ language, setLanguage ] = useState( null );
  const [ search, setSearch ] = useState( null );

    function filesystemlucenenew(props, md5checknew) {
    console.log(path);
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "FILESYSTEMLUCENENEW";
    param.path = path;
    param.md5checknew = md5checknew;
    param.suffix = suffix;
    param.webpath = "task";
    param.async = true;
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
    param.failedlimit = failedlimit;
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

    function consistentclean(props, clean) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "CONSISTENTCLEAN";
      param.clean = clean;
      param.path = path;
    param.webpath = "task";
    setParam(param);
    return;
  }

    function overlapping(props) {
    var param = new ServiceParam();
    param.config = props.config;
    param.function = "OVERLAPPING";
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
        const tables = MyTable.getTabNew(result.list, Date.now(), callbackNewTab);
        callbackNewTab(tables);
      }
    });
  }, [param]);

  useEffect(() => {
    const timer = setInterval(getTask, 60000);
    return () => clearInterval(timer);
  }, []);

  const callbackAsync = useCallback( (uuid) => {
    console.log("typeofuuid" + (typeof uuids));
    uuids.add(uuid);
      setUuids(new Set(uuids));
  }, [uuids]);

  const getTask = async () => {
    for (const id of Array.from(uuids)) {
      const url = Client.geturl("/task/" + id);
      console.log("uuids"+ url);
      const settings = {
        method: 'GET',
      };
      const res = await fetch(url, settings);
      const data = await res.json();
      console.log(data);
      if (data.list != null) {
        console.log(JSON.stringify(data));
        const tables = MyTable.getTabNew(data.list, Date.now(), callbackNewTab);
        callbackNewTab(tables);
        uuids.delete(id);
      }
      setUuids(new Set(uuids));
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
            <DatePicker id="startdatepicker" selected={useStartdate} onChange={(e: Date | null) => setStartdate(e)}/>
          After date
            <DatePicker id="enddatepicker" selected={useEnddate} onChange={(e: Date | null) => setEnddate(e)}/>
        </Nav>
      </Navbar>
      <Nav>
        <Form>
          Failed limit
          <FormControl
            onChange = { (e) => { e.preventDefault(); setFailedlimit(e.target.value) } }
            type="text"/>
        </Form>
      </Nav>
      <Nav>
        <Form>
          Path
          <FormControl
              onChange = { (e) => { e.preventDefault(); setPath(e.target.value) } }
            type="text"/>
        </Form>
      </Nav>
      <Nav>
        <Form>
           Suffix
          <FormControl
              onChange = { (e) => { e.preventDefault(); setSuffix(e.target.value) } }
            type="text"/>
        </Form>
      </Nav>
      <Nav>
        <Form>
          Search
          <FormControl
              onChange = { (e) => { e.preventDefault(); setSearch(e.target.value) } }
            type="text"/>
        </Form>
      </Nav>
      <Nav>
        <Select
                onChange={e => setLanguage(e)}
                options={languages}
        />
      </Nav>
      <Navbar>
           <Navbar.Brand>
            Filesystem add new
          </Navbar.Brand>
        <Nav>
            <Button onClick={ (e) => traverse(props) }>Filesystem add new</Button>
         </Nav>
      </Navbar>
        <Navbar>
          <Navbar.Brand>
            Filesystem add and index
          </Navbar.Brand>
         <Nav>
             <Button onClick={ (e) => filesystemlucenenew(props, false) }>Index filesystem new items</Button>
        </Nav>
         <Nav>
             <Button onClick={ (e) => filesystemlucenenew(props, true) }>Index filesystem changed items</Button>
        </Nav>
      </Navbar>
      <Navbar>
           <Navbar.Brand>
            Index
           </Navbar.Brand>
	  <Nav>
            <Button onClick={ (e) => index(props, false) }>Index</Button>
          <Button onClick={ (e) => index(props, true) }>Reindex</Button>
        </Nav>
      </Navbar>
      <Navbar>
          <Navbar.Brand>
            File system consistency
          </Navbar.Brand>
         <Nav>
             <Button onClick={ (e) => consistentclean(props, false) }>Get consistency</Button>
             <Button onClick={ (e) => consistentclean(props, true) }>Get consistency and clean</Button>
              <Button onClick={ (e) => overlapping(props) }>Get duplicates</Button>
        </Nav>
      </Navbar>
      <Navbar>
           <Navbar.Brand>
            Database
          </Navbar.Brand>
         <Nav>
              <Button onClick={ (e) => dbcheck(props) }>Db check</Button>
          <Button onClick={ (e) => dbindex(props) }>Database search index</Button>
          <Button onClick={ (e) => dbsearch(props) }>Database search</Button>
          <Button onClick={ (e) => deletepathdb(props) }>Delete path</Button>
            <Button onClick={ (e) => notindexed(props) }>Get not yet indexed</Button>
        </Nav>
      </Navbar>
      <Navbar>
         <Navbar.Brand>
            Diagnostics
          </Navbar.Brand>
        <Nav>
            <Button onClick={ (e) => memoryusage(props) }>Memory usage</Button>
        </Nav>
      </Navbar>
    </div>
  );
}

export default memo(ControlPanel);
