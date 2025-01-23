import React, { Component, PureComponent, Fragment } from 'react';

import { Button } from 'react-bootstrap';

import './Main.css';
//import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import { Tabs, Tab } from 'react-bootstrap';

import { Search } from '../Search'
import { Configuration } from '../Configuration'
import { ControlPanel } from '../ControlPanel'
import { Test } from '../test'
//import Misc from '../util'
//import Client from '../util/Client'
import { memo, useCallback, useEffect, useMemo, useState } from "react";
import { MyMap } from '../util'

const tablist = [];

function newtab() {   console.log("bla3")
//main.watchNewTabMainnn
}

function Main ({ props }) {
  const [ tabs, setTabs ] = useState([]);

  function newtab2() {
    console.log("bla4")
    const { main } = props;
    main.watchNewTabMain()
  }

  function newtab3() {
    console.log("bla4")
    //const { main } = this.props;
    console.log(this);
    console.log(this.props);
    //console.log(this.props.updateR3());
    //var me = this.props.updateR3();
    //console.log(me)
    console.log("bla5");
    this.props.newtabMain(['bla']);
  }

  /*
onIncrementAsync() { this.props.incrementasync() }
onIncrement() { this.props.increment() }
onIncrement2() { this.props.increment2() }
  */

  function getanewtab(data, num) {
    return(
      <Tab key={num} eventKey={num} title="Result">
        { data }
      </Tab>
    )
  }

  const callbackNewTab = useCallback( (data) => {
    tabs.push(data);
    setTabs([...tabs]);
    console.log("callb", tabs.length);
    //main.tabs = tabs;
  }, [tabs]);

  const { main } = props;

  const result = main && main.result2 ? main.result2 : null;
  // for testing
  if (result) {
    console.log("result");
  }
  else {
    console.log("noresult");
    return (<div/>);
  }

  console.log("main"+main);
  console.log("CONFIG"+main.config);
   //console.log(main.config.configValueMap);

  var dolucene;
  var dosolr;
  var doelastic;
  if (!!main.config) {
    const configValueMap = MyMap.myget(main.config, 'configValueMap');
    dolucene = MyMap.myget(configValueMap, "searchengine.lucene[@enable]");
    dosolr = MyMap.myget(configValueMap, "searchengine.solr[@enable]");
    doelastic = MyMap.myget(configValueMap, "searchengine.elastic[@enable]");
  }

    const result3 = main && main.result3 ? main.result3 : null;
  const count = main && main.count ? main.count : null;
  //const tabs = main && main.tabs ? main.tabs : null;

  var mytabs = tabs;
  var map = new Object();
  map['title']='tit';
  //var newtab = new Tab(map);
  console.log(tabs);
  var arrayLength = tabs.length;
  console.log("arr");
  console.log(tabs);
  console.log(arrayLength);

    console.log("callt", tabs.length);
    console.log(props);
    console.log(main);
    //console.log(result + " " + result.size);
  if (result /*&& result.size && result.size > 0*/) {
    console.log("callt", tabs.length);
    console.log("cccc" + Object.keys({ ...props }));
    return (
      <Fragment>
        <h1>Aether search engine</h1>
        <h2>H{result3}H{count}H</h2>
        <Tabs defaultActiveKey={1} id="maintabs">
          <Tab eventKey={1} title="Search">
            <h2>Any content 1</h2>
            <Search  dolucene={dolucene} dosolr={dosolr} doelastic={doelastic} props={props} callbackNewTab = {callbackNewTab} />
            <h3>Cont</h3>
          </Tab>
          <Tab eventKey={2} title="Control panel">
            <h2>Any content 2</h2>
            <ControlPanel props={props} callbackNewTab={callbackNewTab}/>
          </Tab>
          <Tab eventKey={3} title="Configuration">
            <h2>Any content 3</h2>
            <Configuration props = {props} config = {main.config} configname = "config" />
          </Tab>
          { mytabs.map((item, index) => getanewtab(item, 4 + index)) }
        </Tabs>
        <Button
          onClick={
            () => { newtab3() }
          }
        >
          New tab
        </Button>
        <Button
          onClick={
            () => { newtab() }
          }
        >
          Async
        </Button>
        <Button
          onClick={
            () => { newtab() }
          }
        >
          Inc
        </Button>
        <Button
          onClick={
            () => { newtab() }
          }
        >
          Inc2
        </Button>
        <div className="mainOutput">
          <p>If you see this screen, it means you are all setup \o/</p>
          <p>The following JSON are showing contents coming from Redux, Saga and Config.</p>
          <pre>
            {JSON.stringify(result/*.toJS()*/, undefined, 2)}
          </pre>
        </div>
      </Fragment>
    );
  }
  return <div />;
}

export default memo(Main);
