import './App.css';
import {BrowserRouter, Routes, Route} from "react-router-dom";
import Home from "./Components/Home";

function App() {
  return (
      <BrowserRouter>
        <div className="App">
          <header className="App-header">
             <Routes>
                 <Route path="/" element={<Home />} />
             </Routes>
          </header>
        </div>
      </BrowserRouter>
  );
}



export default App;



