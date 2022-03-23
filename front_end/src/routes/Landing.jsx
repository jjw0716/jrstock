import { Fade } from "react-awesome-reveal";
import Intro from "../components/Landing/Intro";
import SectionOne from "../components/Landing/SectionOne";
import SectionTwo from "../components/Landing/SectionTwo";
import SectionThree from "../components/Landing/SectionThree";
import Contact from "../components/Landing/Contact";
import Footer from "../components/Landing/Footer";
import ScrollToTop from "../components/Landing/ScrollToTop";
import "../components/Landing/Landing.css";

export default function Landing() {
  return (
    <div className="relative container  max-w-7xl mx-auto px-10">
      <ScrollToTop />
      <Fade direction="up">
        <Intro />
      </Fade>
      <Fade direction="left">
        <SectionOne />
      </Fade>
      <Fade direction="right">
        <SectionTwo />
      </Fade>
      <Fade direction="left">
        <SectionThree />
      </Fade>
      <Contact />
      <Footer />
    </div>
  );
}
