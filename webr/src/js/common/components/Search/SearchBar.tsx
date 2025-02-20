import React from 'react';

import { Client, ConvertToSelect } from '../util'
import { Nav, Navbar, NavItem, Form, FormControl } from 'react-bootstrap';
import { SearchEngineSearchParam } from '../../types/main'
import { MyTable } from '../MyTable'

import { memo, useEffect, useMemo, useState } from "react";

function SearchBar({ text, type, config, callbackNewTab, callbackMLT }) {
  console.log("hhh" + type+ " " + Date.now());
  console.log(text);
  console.log(type);
  const [ searchstring, setSearchstring ] = useState("");
  const [ searchnetstring, setSearchnetstring ] = useState("");
  console.log("searchnetstring" + searchnetstring);

  useEffect(() => {
    console.log("effect" + type  +searchnetstring + "n");
    console.log(searchnetstring);
    if (searchnetstring === "") {
      console.log("effectnot");
      return;
    }
    console.log("effectin");
    console.log(type);
    //console.log(props);
    const param = new SearchEngineSearchParam();
    param.conf = config;
    param.str = searchnetstring;
    param.searchtype = type;
    console.log(searchnetstring + " " + type + " " + searchnetstring);
    console.log(Object.keys(searchnetstring));
    //console.log(Object.keys(props));
    //console.log(Object.keys(searchnetstring.target) + " " + searchnetstring.type);
    //console.log("bbb" + searchnetstring.target.value + " " + type);
    const url = Client.geturl("/search");
    //const url = geturl("/" + param.webpath);
    console.log("xxxx " + url);
    //const bla = Client.fetchApi.search("/search", param);
    //console.log(bla);

    const fetchData = async(url, param) => {
      try {
        console.log("uuuu" + url + " " + JSON.stringify(param));
        const response = await fetch(url, {
          method: "POST",
          headers: {'Accept': 'application/json;charset=utf-8', 'Content-Type': 'application/json',},
          body: JSON.stringify(param),
        });
        const result = await response.json();
        console.log(result);
        const tables = MyTable.getTabNew(result.list, Date.now(), callbackMLT);
        callbackNewTab(tables);
      } catch (error) {
        console.log("error", error);
      }
    };
    console.log("effecteffect" + url);
    fetchData(url, param).catch(console.error);

    console.log("effecteffect2");
  }, [searchnetstring]);
  console.log("effectend");
  return (
    <div>
      <Navbar>
        <Navbar.Brand>
          {text}
        </Navbar.Brand>
        <Nav>
          <Form onSubmit={ (e) => { e.preventDefault(); setSearchnetstring(searchstring) } }>
            <FormControl
              placeholder="Enter text"
              onChange={ (e) => setSearchstring(e.target.value) }
              type="text"/>
          </Form>
        </Nav>
      </Navbar>
    </div>
  );
}

export default memo(SearchBar);
