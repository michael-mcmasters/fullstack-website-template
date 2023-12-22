import {useState, useEffect} from "react";
import logo from './logo.svg';
import './App.css';
import {CONFIG} from "./index.js";

function App() {
  
  const [response, setResponse] = useState({});
  
  useEffect(() => {
    (async function fetchInitialData() {
      console.log("Fetching ...");
      const response = await fetch(CONFIG.ADD_ENDPOINT);
      const responseJson = await response.json();
      setResponse(responseJson);
      console.log("Fetched. Response is: " + responseJson);
    })();
  }, [])
  
  
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Hi there!
          <br />
          {JSON.stringify(response)}
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
