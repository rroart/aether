import { put, fork, takeLatest, takeEvery, call, delay } from 'redux-saga/effects';
import { constants as mainConstants, actions as mainActions } from '../modules/main';
import { Tabs, Tab } from 'react-bootstrap';
import React, { PureComponent } from 'react';

import { Client } from '../../common/components/util'
import { mainType, ServiceParam, SearchEngineSearchParam } from '../../common/types/main'
import { MyTable } from '../../common/components/MyTable'

export function* fetchMainData() {
  // pretend there is an api call
  const result = {
    title: 'Myweb',
    description: __CONFIG__.description,
    source: 'This message is coming from Redux',
  };
  //const result3 = 3;
  console.log("blblfirst");
  yield put(mainActions.updateMain(result));
}

export function* fetchR3() {
  // pretend there is an api call
    const result = 'hei';
  //const result = 3;
  console.log("blbl0");
  yield put(mainActions.updateR3(result));
}

export function* fetchR4() {
  // pretend there is an api call
    const result = 'ieh';
  //const result = 3;
  console.log("blbl1");
  yield put(mainActions.updateR4(result));
}

export function* fetchCount() {
  // pretend there is an api call
    const result = 0;
    //const result = 3;
    //console.log(bl);
  console.log("blbl2");
  //yield put(mainActions.getCount(result));
}

export function* fetchConfig() {
    var serviceparam = new ServiceParam();
    //serviceparam.market = '0';
    console.log("hereconfig");
    let config = yield call(Client.fetchApi.search, "/GETCONFIG", serviceparam);
    console.log("hereconfig2");
    console.log(config);
    const config2 = config;
   yield put(mainActions.setconfig(config2.config));
}

export function* fetchLanguages() {
    var serviceparam = new ServiceParam();
    //serviceparam.market = '0';
    console.log("hereconfig");
    let config = yield call(Client.fetchApi.search, "/getlanguages", serviceparam);
    console.log("hereconfig2");
    console.log(config);
    const config2 = config;
    console.log(config2);
    yield put(mainActions.setLanguages(config2.languages));
}

export function* fetchControl(action) {
    console.log(action)
    const config = action.payload.config;
    const props = action.payload.props;
    const serviceparam = action.payload.param;
    //var serviceparam = new ServiceParam();
    //serviceparam.market = '0';
    console.log("hereconfig");
    console.log(action);
    let result = yield call(Client.fetchApi.search, "/" + serviceparam.webpath, serviceparam);
    const config2 = result;
    console.log(config2);
    const list = result.list;
    const bla = MyTable.t();
    const tab = MyTable.getTab(result.list, Date.now(), props);
    yield put(mainActions.newtabMain(tab));
}

function getMyConfig(config, market, date) {
    const myconfig = new MyConfig();
    myconfig.configTreeMap = config.get('configTreeMap');
    myconfig.configValueMap = config.get('configValueMap');
    myconfig.text = config.get('text');
    myconfig.deflt = config.get('deflt');
    myconfig.type = config.get('type');
    myconfig.date = date;
    myconfig.market = market;
    return myconfig;
}


export function* fetchSearch(action) {
    console.log(action);
    const config = action.payload.config;
    const props = action.payload.props;
    const serviceparam = action.payload.param;
    //const date = config.get('enddate');
    //serviceparam.config = getMyConfig(config, serviceparam.market, date);

    console.log("herecontent");
    //console.log(serviceparam.market);
    let result = yield call(Client.fetchApi.search, "/search", serviceparam)
;
    console.log("herecontent2");
    console.log(data2);
    const bla = MyTable.t("hei");
    console.log(result);
    console.log(action);
    const config2 = result;
    console.log(config2);
    const list = result.list;
    const tab = MyTable.getTab(result.list, Date.now(), props);
    yield put(mainActions.newtabMain(tab));
}

export function* getNewTab() {
    console.log("bla")
    const result = new Tab();
  yield put(mainActions.newtabMain(result));
}

// Our Worker Saga: will perform the ansync increment task
export function* incrementAsync() {
    yield delay(5000); // sleeps for 1 second, yield will suspend the Saga until the Promise completes
    console.log('delay');
    yield put(mainActions.increment());
}

// Our watcher Saga: spawn a new incrementAsync task on each INCREMENT_ASYNC
function* watchIncrementAsync() {
  yield takeEvery(mainConstants.INCREMENT_ASYNC, incrementAsync);
}

function* watchCount() {
  yield takeEvery(mainConstants.GET_COUNT, fetchCount);
}

function* watchGetMain() {
  yield takeLatest(mainConstants.GET_MAIN, fetchMainData);
}

function* watchGetR3() {
  yield takeLatest(mainConstants.GET_R3, fetchR3);
}

function* watchGetR4() {
  yield takeLatest(mainConstants.GET_R4, fetchR4);
}

function* watchNewTabMain() {
  console.log("bla")
  //yield takeLatest(mainConstants.NEWTAB_MAIN, getNewTab);
}

function* watchGetConfig() {
    console.log("watchgetconfig");
  yield takeLatest(mainConstants.GETCONFIG, fetchConfig);
}

function* watchControl() {
    console.log("watchgetcontent");
    //console.log(action);
    //const config = null;
    //console.log(config);
    yield takeEvery(mainConstants.CONTROL, fetchControl);
}

function* watchGetLanguages() {
    console.log("watchgetlanguages");
    //console.log(action);
    //const config = null;
    //console.log(config);
    yield takeEvery(mainConstants.GETLANGUAGES, fetchLanguages);
}

/*
function* watchSearch() {
    console.log("watchgetsearch");
    //console.log(action);
    //const config = null;
    //console.log(config);
    yield takeEvery(mainConstants.SEARCH, fetchSearch);
}
*/

export const mainSaga = [
  fork(watchGetMain),
  fork(watchNewTabMain),
  fork(watchGetR3),
  fork(watchGetR4),
  fork(watchIncrementAsync),
    fork(watchCount),
     fork(watchGetConfig),
  fork(watchGetLanguages),
  fork(watchControl),
    //fork(watchSearch),
];
