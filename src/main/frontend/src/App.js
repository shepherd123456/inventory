import { useEffect } from "react";
import { Route, Routes } from "react-router-dom";

import config from './config.json';
import { useCtx } from './hooks/useCtx';
import useAxios from './hooks/authentication/useAxios';

import Layout from './pages/shared/Layout';

import Unauthorized from "./pages/shared/authentication/Unauthorized";
import RequireAuth from "./pages/shared/authentication/RequireAuth";
import SignUp from "./pages/shared/authentication/SignUp";
import EmailVerified from "./pages/shared/authentication/EmailVerified";
import SignIn from "./pages/shared/authentication/SignIn";

import Home from './pages/home/Home';
import Spreadsheet from './pages/spreadsheet/Spreadsheet';

function App() {
  const { setCtx } = useCtx();
  const axios = useAxios(config);

  useEffect(() => {
    setCtx({
      ...config,
      axios
    });
  }, [])

  return (
    <Routes>
      <Route path="/" element={<Layout />}>

        <Route path='/' element={<Home />} />
        <Route path="sign-up" element={<SignUp />} />
        <Route path="email-verified" element={<EmailVerified />} />
        <Route path="sign-in" element={<SignIn />} />
        <Route path='unauthorized' element={<Unauthorized />} />

        <Route index element={<Home />} />

        <Route element={<RequireAuth />}>
          <Route path='spreadsheet/*' element={<Spreadsheet />} />
        </Route>

      </Route>
    </Routes>
  );
}

export default App;
