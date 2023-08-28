import React from 'react';

import { Client, ConvertToSelect } from '../util'
import SearchBar from './SearchBar';
import { MyTable } from '../MyTable'
import { memo, useCallback, useEffect, useState } from "react";

function Search({dolucene, dosolr, doelastic, props, callbackNewTab}) {
  const [ searchmlt, setSearchmlt ] = useState("");
  console.log(props);

  const callbackMLT = useCallback((result) => {
    console.log("callbackMLT");
    setSearchmlt(result);
  }, []);

  useEffect(() => {
    if (searchmlt === "") {
      return;
    }
    const result = Client.fetchApi.search("/searchmlt", { str : searchmlt });
    result.then(function(result) {
      console.log("callbackMLT", result.list);
      const list = result.list;
      const tables = MyTable.getTabNew(result.list, Date.now(), callbackMLT);
      callbackNewTab(tables);
    });
  }, [searchmlt]);

  const { main } = props;
  let Searchbars=<h2>Waiting for config</h2>;
  console.log(main, dolucene, dosolr, doelastic);
  if (dolucene) {
    Searchbars=<div>
      <SearchBar text='Search standard' type='0' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search analyzing' type='1' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search complexphrase' type='2' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search extendable' type='3' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search multi' type='4' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search surround' type='5' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search classic' type='6' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search simple' type='7' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
    </div>;
  }
  if (dosolr) {
    Searchbars=<div>
      <SearchBar text='Search default' type='0' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search lucene' type='1' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search complexphrase' type='2' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search surround' type='3' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
      <SearchBar text='Search simple' type='4' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
    </div>;
  }
  if (doelastic) {
    Searchbars=<div>
      <SearchBar text='Search' type='0' config={props.config} callbackNewTab={callbackNewTab} callbackMLT={callbackMLT}/>
    </div>;
  }
  return (
    <div>
      { Searchbars }
    </div>
  );
}

export default memo(Search);
