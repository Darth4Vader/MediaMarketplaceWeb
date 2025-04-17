import cartIcon from '../cart.png';
import logo from '../marketplace_logo.png';
import searchIcon from '../search.png';
import userIcon from '../user.png';
import React, {Suspense, useEffect, useState, use} from 'react';
import { Link } from 'react-router-dom'
import { getAllMovies, getAllMovies2 } from '../http/api';

export default function AppBar() {

    const [searchText, setSearchText] = useState('');
    const [userMessage, setUserMessage] = useState('User Not Logged');

    const handleSearch = () => {
        //App.getApplicationInstance().enterSearchPage(searchText);
    };

    const enterCart = () => {
        //App.getApplicationInstance().enterCartOrAddMovies();
    };

    const enterHome = () => {
        //App.getApplicationInstance().changeAppPanel(HomePageController.PATH);
    };

    const enterUserPage = () => {
        //App.getApplicationInstance().changeAppPanel(UserPageController.PATH);
    };
    /*return (
        <div className="app-bar" style={
            {
                //display: 'flex',
                //justifyContent: 'space-between',
                alignItems: 'center',
                padding: '10px 20px',
                backgroundColor: '#f8f8f8',
                borderBottom: '1px solid #ccc',
                height: '10vh',
                width: '100%',
            }
        }>
        </div>
    );*/
    return (
        <div className="app-bar" style={
            {
                display: 'flex',
                //justifyContent: 'space-between',
                alignItems: 'center',
                //padding: '10px 20px',
                backgroundColor: '#f8f8f8',
                borderBottom: '1px solid #ccc',
                height: '10vh',
                //maxWidth: '100%',
                //width: '100%',
                gap: '10px',
                //marginRight: '50px',
            }
        }>
            <img src={cartIcon} alt="Cart" className="icon" onClick={enterCart} height='100%'/>
            <div style={{height: "100%"}}>
                <Link to="/">
                    <img src={logo} alt="Home" className="icon" onClick={enterHome} height="100%"/>
                </Link>
            </div>

            <div className="search-bar" style={
                {
                    display: 'flex',
                    alignItems: 'center',
                    gap: '2px',
                    height: '50%',
                    border: '1px solid #ccc',
                    //containerType: 'inline-size',
                    //boxSizing: 'border-box'
                }
            }>
                <img src={searchIcon} alt="Search" className="search-icon" onClick={handleSearch} height="100%"/>
                <input
                    type="text"
                    value={searchText}
                    onChange={(e) => setSearchText(e.target.value)}
                    placeholder="Search..."
                    style={
                        {
                            height: '100%',
                            //fontSize: '5cqw'
                        }
                    }
                />
            </div>

            <div className="user-section" style={
                {
                    height: '100%',
                }
            }>
                <img src={userIcon} alt="User" className="user-icon" onClick={enterUserPage} height="100%"/>
                <label>{userMessage}</label>
            </div>
        </div>
    );
}