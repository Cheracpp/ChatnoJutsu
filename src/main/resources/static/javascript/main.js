'use strict';


let username = localStorage.getItem('username');

const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const searchInput = document.querySelector('#search')
const connectingElement = document.querySelector('.connecting');
const chatMessagesSpace = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');
const search = document.querySelector('#people')
const chatArea = document.querySelector('.chat-area')
const searchArea = document.querySelector('.search-area')

let selectedUserId = null;
let stompClient = null;

const socket = new SockJS('/ws');
stompClient = Stomp.over(socket);

stompClient.connect({}, onConnected, onError);

function onConnected() {
    console.log("onConnected");
    stompClient.subscribe(`/user/${username}/queue/messages`, onMessageReceived, (error) => {
        console.error("Error during subscription", error);
    });
    document.querySelector('#connected-user-fullname').textContent = username;
    fetchAndDisplayUsers().then();
}

async function fetchAndDisplayUsers() {
    console.log("DisplayUsers");
    const usersResponse = await fetch(`/users/${username}/chats`);
    let users = await usersResponse.json();

    users = users.filter(aUsername => aUsername !== username);

    const usersList = document.getElementById('connectedUsers');
    usersList.innerHTML = '';

    users.forEach((aUsername, index) => {
        appendUserElement(aUsername, usersList);

        if (index < users.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            usersList.appendChild(separator);
        }
    });
}

function appendUserElement(aUsername, usersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = aUsername;

    const userImage = document.createElement('img');
    userImage.src = '../images/user_icon.png';
    userImage.alt = aUsername;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = aUsername;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '0';
    receivedMsgs.classList.add('nbr-msg', 'hidden');

    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);
    if(usersList.id === 'connectedUsers' ) {
        listItem.addEventListener('click', userItemClick);
    }else{
        listItem.addEventListener('click', searchItemClick);
    }

    usersList.appendChild(listItem);
}
function searchItemClick(event){
    let found = false;
    const usersList = document.getElementById('connectedUsers');
    const clickedUser = event.currentTarget;
    selectedUserId = clickedUser.getAttribute('id');
    let activeUser;
    if (usersList) {
        for (let listItem of usersList.children) {
            if(listItem.id === selectedUserId){
                activeUser = listItem;
                found = true;
            }
            console.log(listItem.id);
        }
    }
    if(found === false){
        activeUser = document.createElement('li');

        activeUser.classList.add('user-item');
        activeUser.id = selectedUserId;

        const userImage = document.createElement('img');
        userImage.src = '../images/user_icon.png';
        userImage.alt = selectedUserId;

        const usernameSpan = document.createElement('span');
        usernameSpan.textContent = selectedUserId;

        const receivedMsgs = document.createElement('span');
        receivedMsgs.textContent = '0';
        receivedMsgs.classList.add('nbr-msg', 'hidden');

        activeUser.appendChild(userImage);
        activeUser.appendChild(usernameSpan);
        activeUser.appendChild(receivedMsgs);
        usersList.insertBefore(activeUser,usersList.firstChild)
    }

    messageForm.classList.remove('hidden');
    chatArea.classList.remove("hidden");
    searchArea.classList.add("hidden");
    activeUser.classList.add('active');
    fetchAndDisplayUserChat().then();
    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';
    activeUser.addEventListener('click', userItemClick);

}
function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');
    chatArea.classList.remove("hidden");
    searchArea.classList.add("hidden");
    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    console.log("the clicked User is: " + selectedUserId)
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';
}

function displayMessage(messageFrom, content) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (messageFrom === username) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatMessagesSpace.appendChild(messageContainer);
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${username}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    chatMessagesSpace.innerHTML = '';
    userChat.forEach(Message => {
        displayMessage(Message.messageFrom, Message.content)
    });
    chatMessagesSpace.scrollTop = chatMessagesSpace.scrollHeight;
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const Message = {
            messageFrom: username,
            messageTo: selectedUserId,
            content: messageContent,
            createdAt: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(Message));
        displayMessage(username, messageContent);
        messageInput.value = '';
    }
    chatMessagesSpace.scrollTop = chatMessagesSpace.scrollHeight;
    event.preventDefault();
}

async function onMessageReceived(payload) {
    await fetchAndDisplayUsers();
    console.log('Message received', payload);
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.messageFrom) {
        displayMessage(message.messageFrom, message.content);
        chatMessagesSpace.scrollTop = chatMessagesSpace.scrollHeight;
    }

    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }

    const notifiedUser = document.querySelector(`#${message.messageFrom}`);
    if (notifiedUser && !notifiedUser.classList.contains('active')) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
}

function onSearch() {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    chatArea.classList.add("hidden");
    searchArea.classList.remove("hidden");

}
async function getUsers(){
    const searchContent = searchInput.value.trim();
    const usersList = document.getElementById('results');
    usersList.innerHTML = ''; // Clear previous results

    if (searchContent) {
        try {
            const usersResponse = await fetch(`/users/searchUsers?query=${searchContent}`);

            if (!usersResponse.ok) {
                throw new Error(`Error: ${usersResponse.status}`);
            }

            let users = await usersResponse.json();

            users = users.filter(aUsername => (aUsername !== username));

            if (users.length === 0) {
                const noResults = document.createElement('li');
                noResults.innerText = 'No users found';
                usersList.appendChild(noResults);
                return;
            }

            users.forEach((aUsername, index) => {
                appendUserElement(aUsername, usersList);

                if (index < users.length - 1) {
                    const separator = document.createElement('li');
                    separator.classList.add('separator');
                    usersList.appendChild(separator);
                }
            });
        } catch (error) {
            console.error('Error fetching users:', error);
            const errorMessage = document.createElement('li');
            errorMessage.innerText = 'Error fetching users. Please try again.';
            usersList.appendChild(errorMessage);
        }
    } else {
        const noInputMessage = document.createElement('li');
        noInputMessage.innerText = 'Please enter a search term.';
        usersList.appendChild(noInputMessage);
    }
}


function onLogout() {
    fetch("/auth/logout", {method: "POST"})
        .then(response => {
            if (response.ok) {
                console.log("Logout successful");
                window.location.href = "http://localhost:8080/login";
            } else {
                console.log("Logout failed: " + response.status);
            }
        })
        .catch(error => {
            console.log("Error during logout: ", error);
        });
}

messageForm.addEventListener('submit', sendMessage, true);
search.addEventListener('click', onSearch, true);
logout.addEventListener('click', onLogout, true);