/* eslint-disable no-undef */

import { env } from '../../../../env'

function getPort() {
    if (typeof env.REACT_APP_MYPORT !== 'undefined') {
        return env.REACT_APP_MYPORT;
    }
    return 23456;
    // return 80;
}

function getHost() {
    console.log("pppp");
    console.log(process.env);
    if (typeof env.REACT_APP_MYSERVER !== 'undefined') {
        return env.REACT_APP_MYSERVER;
    }
    return "localhost";
}


function searchn(query, cb) {
  return fetch(`http://localhost:8080` + query, {
    accept: 'application/json',
  }).then(checkStatus)
    .then(parseJSON)
    .then(cb)
    .catch((error) => console.log(error.message));
}

function search(query, serviceparam, cb) {
    console.log("hhh");
    console.log(JSON.stringify(serviceparam));
    /*
  var bla = fetch(`http://localhost:22345` + query, {
      method: "POST",
      headers: { 'Accept': 'application/json;charset=utf-8', 'Content-Type': 'application/json', },
      body: JSON.stringify(serviceparam),
  }).then(checkStatus);
    console.log(bla);
*/
    return fetch("http://" + getHost() + ":" + getPort() + query, {
      method: "POST",
      headers: { 'Accept': 'application/json;charset=utf-8', 'Content-Type': 'application/json', },
      body: JSON.stringify(serviceparam),
  }).then(checkStatus)
        .then(parseJSON)
    //.then(console.log)
    .then(cb)
    .catch((error) => console.log(error.message));
}

function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  } else {
    const error = new Error(`HTTP Error ${response.statusText}`);
    error.status = response.statusText;
    error.response = response;
    console.log(error); // eslint-disable-line no-console
    throw error;
  }
}

function parseJSON(response) {
  return response.json();
}

const fetchApi = {
    search(query, serviceparam) {
        console.log(query);
        console.log(JSON.stringify(serviceparam));
        return fetch("http://" + getHost() + ":" + getPort() + query, {
            method: "POST",
            headers: { 'Accept': 'application/json;charset=utf-8', 'Content-Type': 'application/json', },
            body: JSON.stringify(serviceparam),
        })
            .then(statusHelper)
            .then(parseJSON)
            .catch((error) => console.log(error.message))
            .then (data => data)
    }
}

function statusHelper (response) {
  if (response.status >= 200 && response.status < 300) {
    return Promise.resolve(response)
  } else {
    return Promise.reject(new Error(response.statusText))
  }
}

const Client = { search, fetchApi };
export default Client;
