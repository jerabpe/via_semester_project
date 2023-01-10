const MOVIES_NOW_URL = 'http://localhost:8080/movie/now';

const welcomePage = document.querySelector("#welcomePage");
const letsWatchButton = document.querySelector("#letsWatchButton");
const mainPage = document.querySelector("#mainPage");
const homeNavButton = document.querySelector("#homeNavButton");
const loading = document.querySelector("#loading");
const myListsButton = document.querySelector("#myListsButton");
const newListButton = document.querySelector("#newListButton");
const createListPage = document.querySelector("#createListPage");
const createListButton = document.querySelector("#createListButton");
const select = document.querySelector("#listSelect");
const addMovie = document.querySelector("#addMovieModal");
const addMovieButton = document.querySelector("#addButton");
const closeModalButton = document.querySelector("#closeModalButton");
const logoutButton = document.querySelector("#logoutButton");
const listsPage = document.querySelector("#listsPage");
const listsGrid = document.querySelector("#listsGrid");

let shownPage = welcomePage;

let selectedMovie = 0;
let selectedList = 0;
let watchLists = [];

function hideAll(){
    welcomePage.classList.add("d-none");
    mainPage.classList.add("d-none");
    createListPage.classList.add("d-none");
    addMovie.classList.add("d-none");
    listsPage.classList.add("d-none");
}

homeNavButton.addEventListener("click", e => {
    hideAll();
    shownPage.classList.add("d-none");
    welcomePage.classList.remove("d-none");
    shownPage = welcomePage;
    //mainPage.classList.add("d-none");
});

let loadedIFrames = 0;
letsWatchButton.addEventListener("click", e => {
    shownPage.classList.add("d-none");
    //welcomePage.classList.add("d-none");
    mainPage.classList.remove("d-none");
    shownPage = mainPage;
    if(loadedIFrames < 20){
        loading.classList.remove("d-none");
    }
});

window.addEventListener("load", e => {
    //call server
    fetch(MOVIES_NOW_URL, {
        credentials: 'include'
    })
        .then((response) => response.json())
        .then(data => {
            console.log(data);
            for(m of data){
                //console.log(m.title);
                //console.log(JSON.parse(m.trailer));
                const el = document.createElement("div");
                el.classList.add("movie");
                el.classList.add("container");
                el.innerHTML = `
                    <h3 class="text-center fs-3 fw-semibold">${m.title}</h3>
                    <p class="fs-5 text-center">${m.description}</p>
                    <div class="d-flex justify-content-center">
                        ${JSON.parse(m.trailer).embedHtml}
                    </div>
                    <div class="d-flex justify-content-center py-2">
                        <button id="${m.id}" type="button" class="addButton d-none btn btn-outline-primary btn-sm">
                            Add to Watch List
                        </button>
                    </div>
                    `;
                mainPage.appendChild(el);
                mainPage.appendChild(document.createElement("hr"));
            }

            const iFrames = document.querySelectorAll("iframe");
            for(i of iFrames){
                i.addEventListener("load", e => {
                    loadedIFrames += 1;
                    if(loadedIFrames == 20 && !mainPage.classList.contains("d-none")){
                        mainPage.classList.remove("d-none");
                        loading.classList.add("d-none");
                    }
                });
            }

            const addButtons = document.querySelectorAll(".addButton");
            for(b of addButtons){
                b.addEventListener("click", e => {
                    e.preventDefault();
                    select.innerHTML = '';
                    fetch('http://localhost:8080/list', {
                        credentials: 'include'
                    })
                    .then(response => response.json())
                    .then(lists => {
                        console.log(lists);
                        let index = 0;
                        watchLists = lists;
                        for(l of lists){
                            const opt = document.createElement("option");
                            opt.value = l.id;
                            opt.innerHTML = l.title;
                            if(index == 0){
                                opt.selected = true;
                            }
                            index++;
                            select.append(opt);
                        }
                    });
                    selectedMovie = e.path[0].id;
                    addMovie.classList.remove("d-none");
                    shownPage.classList.add("d-none");
                });
            }
        });
        
});

const loginNavButton = document.querySelector("#loginNavButton");
const loginPage = document.querySelector("#loginPage");
const signUpButton = document.querySelector("#signUpButton");
const registerPage = document.querySelector("#registerPage");
const userNavDropdown = document.querySelector("#userNavDropdown");
const userNavDropdownA = document.querySelector("#userNavDropdownA");

loginNavButton.addEventListener("click", e => {
    e.preventDefault();
    //welcomePage.classList.add("d-none");
    shownPage.classList.add("d-none");
    loginPage.classList.remove("d-none");
});

signUpButton.addEventListener("click", e => {
    e.preventDefault();
    shownPage.classList.add("d-none");
    registerPage.classList.remove("d-none");
    shownPage = registerPage;
});

const loginButton = document.querySelector("#loginButton");
loginButton.addEventListener("click", e => {
    const username = document.querySelector("#login").value;
    const password = document.querySelector("#loginPassword").value;
    const data = new FormData();
    data.append("username", username);
    data.append("password", password);
    //validate
    //send request
    fetch('http://localhost:8080/login', {
        method: 'POST',
        credentials: 'include',
        body: data
    })
    .then(response =>  response.json())
    .then(response => {
        console.log(response);
        userNavDropdownA.innerHTML = response.username;
    });
    loginPage.classList.add("d-none");
    shownPage.classList.remove("d-none");
    userNavDropdown.classList.remove("d-none");
    loginNavButton.classList.add("d-none");

    const addButtons = document.querySelectorAll(".addButton");
    for(b of addButtons){
        b.classList.remove("d-none");
    }

    //todo - show Add Movie to list buttons!
});

newListButton.addEventListener("click", e => {
    e.preventDefault();
    shownPage.classList.add("d-none");
    createListPage.classList.remove("d-none");
    //shownPage = createListPage;
});

createListButton.addEventListener("click", e => {
    e.preventDefault();
    const titleInput = document.querySelector("#listTitle").value;
    //validate

    //send
    fetch('http://localhost:8080/list', {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ title: titleInput })
    }).then(res => res.json()).then(res => console.log(res));
    createListPage.classList.add("d-none");
    shownPage.classList.remove("d-none");
});

addMovieButton.addEventListener("click", e => {
    const listId = select.options[select.selectedIndex].value;
    console.log(listId);
    console.log(selectedMovie);
    //const listId = select
    fetch('http://localhost:8080/list/'+listId+'/add?movieId='+selectedMovie, {
        method: 'PUT',
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => console.log(data));

    addMovie.classList.add("d-none");
    shownPage.classList.remove("d-none");
});

closeModalButton.addEventListener("click", e => {
    addMovie.classList.add("d-none");
    shownPage.classList.remove("d-none");
});

logoutButton.addEventListener("click", e => {
    fetch('http://localhost:8080/logout', {
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => console.log(data));
    userNavDropdown.classList.add("d-none");
    loginNavButton.classList.remove("d-none");
    const addButtons = document.querySelectorAll(".addButton");
    for(b of addButtons){
        b.classList.add("d-none");
    }
    if(shownPage == listsPage){
        hideAll();
        welcomePage.classList.remove("d-none");
    }
});

myListsButton.addEventListener("click", e => {
    listsGrid.innerHTML = '';
    fetch('http://localhost:8080/list', {
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
        console.log(data);
        for(l of data){
            const ul = document.createElement("ul");
            for(m of l.movies){
                const li = document.createElement("li");
                li.innerHTML = m.title;
                ul.append(li);
            }
            const el = document.createElement("div");
            el.classList.add("col-4");
            el.innerHTML = `
                <h2>${l.title}</h2>
                <ul>
                    ${ul.innerHTML}
                </ul>
            `;
            listsGrid.append(el);
        }
    });

    shownPage.classList.add("d-none");
    listsPage.classList.remove("d-none");
    shownPage = listsPage;
});