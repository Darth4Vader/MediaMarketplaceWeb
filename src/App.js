import logo from './logo.svg';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import HomePage from "./components/HomePage";
import LoadMoviePage from "./components/MoviePage";


function App() {
  return (
      <div className="App">
        <header className="App-header">
          <Router>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/movie/:id" element={<LoadMoviePage />} />
            </Routes>
          </Router>
        </header>
      </div>
  );
}

/*
function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
          <HomePage></HomePage>
      </header>
    </div>
  );
}
*/

export default App;
