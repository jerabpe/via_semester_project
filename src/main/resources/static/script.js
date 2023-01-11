const BASE_URL = 'http://localhost:8080';
const MOVIES_NOW_URL = BASE_URL + '/movie/now';

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
const loginNavButton = document.querySelector("#loginNavButton");
const loginPage = document.querySelector("#loginPage");
const signUpButton = document.querySelector("#signUpButton");
const registerPage = document.querySelector("#registerPage");
const userNavDropdown = document.querySelector("#userNavDropdown");
const userNavDropdownA = document.querySelector("#userNavDropdownA");
const registerButton = document.querySelector("#registerButton");
const editListPage = document.querySelector("#editListPage");
const loginButton = document.querySelector("#loginButton");
const saveListEditButton = document.querySelector("#saveListEditButton");

let shownPage = welcomePage;
let loggedUser = "";
let editedList = 0;

let selectedMovie = 0;
let selectedList = 0;
let watchLists = [];

function hideAll(){
    welcomePage.classList.add("d-none");
    mainPage.classList.add("d-none");
    createListPage.classList.add("d-none");
    addMovie.classList.add("d-none");
    listsPage.classList.add("d-none");
    loginPage.classList.add("d-none");
    registerPage.classList.add("d-none");
    editListPage.classList.add("d-none");
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
            //console.log(data);
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
                    fetch(BASE_URL + '/list', {
                        credentials: 'include'
                    })
                    .then(response => response.json())
                    .then(lists => {
                        //console.log(lists);
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

loginNavButton.addEventListener("click", e => {
    e.preventDefault();
    //welcomePage.classList.add("d-none");
    shownPage.classList.add("d-none");
    loginPage.classList.remove("d-none");
});

signUpButton.addEventListener("click", e => {
    e.preventDefault();
    hideAll();
    shownPage.classList.add("d-none");
    registerPage.classList.remove("d-none");
});

loginButton.addEventListener("click", e => {
    const username = document.querySelector("#login").value;
    const password = document.querySelector("#loginPassword").value;
    login(username, password);
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
    fetch(BASE_URL + '/list', {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ title: titleInput })
    }).then(res => res.json()).then(res => {
        console.log(res);
        loadLists();
        createListPage.classList.add("d-none");
        shownPage.classList.remove("d-none");
    });
});

addMovieButton.addEventListener("click", e => {
    const listId = select.options[select.selectedIndex].value;
    //console.log(listId);
    //console.log(selectedMovie);
    //const listId = select
    fetch(BASE_URL + '/list/'+listId+'/add?movieId='+selectedMovie, {
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
    fetch(BASE_URL + '/logout', {
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => console.log(data));
    userNavDropdown.classList.add("d-none");
    loginNavButton.classList.remove("d-none");
    const addButtons = document.querySelectorAll(".addButton");
    loggedUser = "";
    for(b of addButtons){
        b.classList.add("d-none");
    }
    if(shownPage == listsPage){
        hideAll();
        welcomePage.classList.remove("d-none");
    }
});

myListsButton.addEventListener("click", e => {
    hideAll();
    loadLists();
    listsPage.classList.remove("d-none");
    shownPage = listsPage;
});

registerButton.addEventListener("click", e => {
    e.preventDefault();
    const username = document.querySelector("#registerUsername").value;
    const password = document.querySelector("#registerPassword").value;
    const password2 = document.querySelector("#registerPasswordAgain").value;
    //validate

    //send
    const data = { username: username, password: password };
    fetch(BASE_URL + '/user', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        //console.log(response);
        if(response.status == 409){
            alert("Username already taken, please try a different one.");
            return;
        }
        if(response.status == 201){
            login(username, password);
            registerPage.classList.add("d-none");
            shownPage.classList.remove("d-none");
        }
        response.json();
    })
    .then(data => {
        //console.log(data);
    });
})

function login(username, password){
    const data = new FormData();
    data.append("username", username);
    data.append("password", password);
    fetch(BASE_URL + '/login', {
        method: 'POST',
        credentials: 'include',
        body: data
    })
    .then(response =>  response.json())
    .then(response => {
        //console.log(response);
        if(response.username == null){
            alert("Incorrect username or password.");
        } else {
            userNavDropdownA.innerHTML = response.username;
            loggedUser = response.username;
            loginPage.classList.add("d-none");
            shownPage.classList.remove("d-none");
            userNavDropdown.classList.remove("d-none");
            loginNavButton.classList.add("d-none");
        }
    });
    

    const addButtons = document.querySelectorAll(".addButton");
    for(b of addButtons){
        b.classList.remove("d-none");
    }
}

function loadLists(){
    document.querySelector("#listsPage > div > h2").innerHTML = `Watch Lists for ${loggedUser}`;
    listsGrid.innerHTML = '';
    fetch(BASE_URL + '/list', {
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
        //console.log(data);
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
                <div class="d-flex flex-row justify-content-between">
                    <h3 class="text-center fs-3 fw-semibold">${l.title}</h3>
                    <div>
                        <button type="button" title="${l.id}" class="edit btn btn-light">
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" class="bi bi-pencil-square" viewBox="0 0 16 16">
                                <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                                <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5v11z"/>
                            </svg>
                        </button>
                        <button type="button" title="${l.id}" class="trashcan btn btn-light">
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="red" class="bi bi-trash" viewBox="0 0 16 16">
                                <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"></path>
                                <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"></path>
                            </svg>
                        </button>
                    </div>
                </div>
                <ul>
                    ${ul.innerHTML}
                </ul>
            `;
            listsGrid.append(el);
        }
        const delButtons = document.querySelectorAll("button.trashcan");
        for(b of delButtons){
            b.addEventListener("click", e => {
                e.preventDefault();
                //console.log(e.path);
                //console.log(b.title);
                let btn;
                for(el of e.path){
                    if(el.nodeName == "BUTTON"){
                        btn = el;
                        break;
                    }
                }
                let id = btn.title;
                //console.log(id);
                if(confirm("Are you sure you want to delete this list?")){
                    //send req
                    deleteList(id);
                }
            });
        }
        const editButtons = document.querySelectorAll("button.edit");
        for(b of editButtons){
            b.addEventListener("click", e => {
                e.preventDefault();
                //console.log(e.path);
                //console.log(b.title);
                let btn;
                for(el of e.path){
                    if(el.nodeName == "BUTTON"){
                        btn = el;
                        break;
                    }
                }
                let id = btn.title;
                editedList = id;
                fetch(BASE_URL + "/list/" + id, {
                    credentials: 'include'
                }).then(response => response.json())
                .then(data => {
                    //console.log(data);
                    const title = data.title;
                    const movies = data.movies;
                    const checkList = document.querySelector("#editListCheckList");
                    checkList.innerHTML = '';
                    let idx = 111;
                    for(m of movies){
                        const el = document.createElement("div");
                        el.classList.add("form-check");
                        el.classList.add("form-white");
                        el.innerHTML = `
                        <input type="radio" class="form-check-input" id="${idx}" value="${m.id}" name="editListRadio">
                        <label class="form-check-label" for="${idx}">
                          ${m.title}
                        </label>
                        `;
                        checkList.append(el);
                        idx++;
                    }
                })

                shownPage.classList.add("d-none");
                editListPage.classList.remove("d-none");
            });
        }
    });
}

function deleteList(id){
    fetch(BASE_URL + '/list/' + id, {
        method: 'DELETE',
        credentials: 'include'
    })
    .then(response => {
        if(response.ok){
            loadLists();
        }
    });
}

saveListEditButton.addEventListener("click", e => {
    e.preventDefault();
    const checklist = document.querySelector("#editListCheckList");
    //console.log(checklist);
    let movie;
    for(const child of checklist.children){
        if(child.children[0].checked){
            movie = child.children[0].value;
            break;
        }
    }
    console.log(movies);
    removeMoviesFromList(editedList, movies);
});

function removeMoviesFromList(listId, movieId){
    fetch(BASE_URL + "/list/" + listId + "/remove?movieId=" + movieId, {
        method: 'PUT',
        credentials: 'include'
    }).then(response => response.json())
    .then(data => {
        console.log(data);
        loadLists();
        editListPage.classList.add("d-none");
        shownPage.classList.remove("d-none");
    });
}