import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {default as LOCAL_CONFIG} from "./environments/local"
import {default as DEV_CONFIG} from "./environments/dev.js"
import {default as PROD_CONFIG} from "./environments/prod.js"

export const CONFIG = getEnvConfig();

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();


function getEnvConfig() {
  switch (process.env.REACT_APP_ENV) {
    case ("local"):
      console.log("Running local configuration");
      return LOCAL_CONFIG;
    case ("dev"):
      console.log("Running dev configuration");
      return DEV_CONFIG;
    case ("prod"):
      console.log("Running prod configuration");
      return PROD_CONFIG;
    default:
      console.error("Unable to find environment configuration for " + process.env.REACT_APP_MY_ENV + ". Defaulting to local config");
      return LOCAL_CONFIG;
  }
}