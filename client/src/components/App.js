import Instructions from "../components/Instructions";
import axios from "axios";
import Challenge from "../components/Challenge";
import "../styles.css";


export default function App() {
  return (
    <>
      <Instructions />
      <div className="challenge-container">
        <Challenge />
      </div>
    </>
  );
}
