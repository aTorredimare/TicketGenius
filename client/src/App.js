import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AppLayout, BrowseProducts, DefaultRoute, Profile, AddProfile, UpdateProfile, Login, Signup, HomeByRole, ManagerHome, 
        CustomerExpertHome, TicketDetails, NewTicket, PurchasedProducts, AddExpert, NewSale } from './components/View';
import AuthenticationProvider from './components/contexts/AuthenticationContext';
import RequireAuth from './components/AuthComponents/RequireAuth';

function App() {

  return (
    <>
      <AuthenticationProvider>
          <BrowserRouter>
            <Routes>
              <Route path='/' element={<AppLayout />}>
              <Route path='/' element={<HomeByRole />} />
                <Route index path='login' element={<Login />} />
                <Route path='signup' element={<Signup />} />
                <Route path='dashboard' element={<RequireAuth roles={['Manager']}><ManagerHome /></RequireAuth>} />
                <Route path='manage_experts' element={<RequireAuth roles={['Manager']}><AddExpert /></RequireAuth>} />
                <Route path='addsale' element={<RequireAuth roles={['Manager']}><NewSale /></RequireAuth>} />
                <Route path='tickets' element={<RequireAuth roles={['Expert', 'Client']}><CustomerExpertHome /></RequireAuth>} />
                <Route path='tickets/:id' element={<RequireAuth roles={['Expert', 'Client']}><TicketDetails /></RequireAuth>} />
                <Route path='new_ticket' element={<RequireAuth roles={['Client']}><NewTicket /></RequireAuth>} />
                <Route path='profiles/:email' element={<RequireAuth roles={['Manager', 'Expert', 'Client']}><Profile /></RequireAuth>} />
                <Route path='products' element={<RequireAuth roles={['Manager', 'Client']}><BrowseProducts /></RequireAuth>} />
                <Route path='purchased_products' element={<RequireAuth roles={['Client']}><PurchasedProducts /></RequireAuth>} />
                <Route path='addprofile' element={<AddProfile />} />
                <Route path='updateprofile/:email' element={<RequireAuth roles={['Manager']}><UpdateProfile /></RequireAuth>} />
              </Route>
              <Route path='*' element={<DefaultRoute />} />
            </Routes>
          </BrowserRouter>
      </AuthenticationProvider>
    </>
  )
}

export default App;
