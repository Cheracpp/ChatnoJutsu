'use strict';

const currentUser = loggedInUser;
if (currentUser == null) {
  window.location.href = '/login';
}

const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const searchInput = document.querySelector('#search')
const connectingElement = document.querySelector('.connecting');
const chatMessagesSpace = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');
const search = document.querySelector('#people')
const chatArea = document.querySelector('.chat-area')
const searchArea = document.querySelector('.search-area')
const roomsList = document.getElementById('connectedUsers');

let selectedRoomId = null;
let stompClient = null;
let roomsDetails = null;
let usersDetails = null;

// Utility function to get CSRF token from cookie
function getCSRFToken() {
  const cookies = document.cookie.split(';');
  for (let cookie of cookies) {
    const [name, value] = cookie.trim().split('=');
    if (name === 'XSRF-TOKEN') {
      return decodeURIComponent(value);
    }
  }
  return null;
}

// Utility function to create headers with CSRF token
function createAuthHeaders() {
  const csrfToken = getCSRFToken();
  const headers = {
    'Content-Type': 'application/json'
  };
  if (csrfToken) {
    headers['X-XSRF-TOKEN'] = csrfToken;
  }
  return headers;
}

const socket = new SockJS('/ws');
stompClient = Stomp.over(socket);

stompClient.connect({}, onConnected, onError);

function onConnected() {
  stompClient.subscribe(`/user/queue/private`, onMessageReceived, (error) => {
  });
  document.querySelector(
      '#connected-user-fullname').textContent = currentUser.username;
  fetchAndDisplayUsers().then();
}

async function fetchAndDisplayUsers() {
  const roomsResponse = await fetch(`/rooms`, {
    method: 'GET',
    headers: createAuthHeaders()
  });
  if (!roomsResponse.ok) {
    throw new Error(
        `error while fetching users, http status code: ${roomsResponse.status}`);
  }

  const rooms = await roomsResponse.json();
  roomsDetails = rooms.reduce((acc, room) => {
    acc[room.roomId] = room;
    return acc;
  }, {});

  // assuming only direct rooms exist
  let users = [...new Set(rooms.flatMap((room) => room.participants)
  .filter(participantId => participantId !== currentUser.id))];
  if (!users) {
    users = [];
  }

  // fetch for users' details
  const usersDetailsResponse = await fetch("users/details", {
    method: 'POST',
    headers: createAuthHeaders(),
    body: JSON.stringify(users)
  })

  if (!usersDetailsResponse.ok) {
    throw new Error("error fetching users details, http status code: "
        + usersDetailsResponse.status);
  }
  usersDetails = await usersDetailsResponse.json();

  roomsList.innerHTML = '';
  rooms.forEach((room, index) => {
    appendRoomElement(room);

    if (index < users.length - 1) {
      const separator = document.createElement('li');
      separator.classList.add('separator');
      roomsList.appendChild(separator);
    }
  });
}

function appendRoomElement(room) {
  const participants = room.participants;
  const receiverId = participants.find(id => id !== currentUser.id);
  const receiver = receiverId ? usersDetails[receiverId].username
      : 'Unknown User';

  const listItem = document.createElement('li');
  const usernameSpan = document.createElement('span');
  const userImage = document.createElement('img');

  listItem.classList.add('room-item');
  listItem.id = room.roomId;

  if (room.type === 'direct') {
    userImage.src = '../images/user_icon.png';
    userImage.alt = receiver;
    usernameSpan.textContent = receiver;
  } else {
    const numberOfParticipants = participants.length;
    userImage.src = '../images/group_icon.png';
    if (room.name === null) {
      room.name = '';
    }
    userImage.alt = room.name + "group image";
    usernameSpan.textContent = receiver + ` and ${numberOfParticipants
    - 2} others`;
  }

  const receivedMsgs = document.createElement('span');
  receivedMsgs.textContent = '0';
  receivedMsgs.classList.add('nbr-msg', 'hidden');

  listItem.appendChild(userImage);
  listItem.appendChild(usernameSpan);
  listItem.appendChild(receivedMsgs);
  listItem.addEventListener('click', roomItemClick);

  roomsList.appendChild(listItem);
}

function roomItemClick(event) {
  document.querySelectorAll('.room-item').forEach(item => {
    item.classList.remove('active');
  });
  messageForm.classList.remove('hidden');
  chatArea.classList.remove("hidden");
  searchArea.classList.add("hidden");
  const clickedRoom = event.currentTarget;
  clickedRoom.classList.add('active');

  selectedRoomId = clickedRoom.getAttribute('id');
  fetchAndDisplayUserChat().then();

  const nbrMsg = clickedRoom.querySelector('.nbr-msg');
  nbrMsg.classList.add('hidden');
  nbrMsg.textContent = '0';
}

function displayMessage(senderId, content) {
  const messageContainer = document.createElement('div');
  messageContainer.classList.add('message');
  if (senderId === currentUser.id) {
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
  const userChatResponse = await fetch(`/messages/${selectedRoomId}`, {
    method: 'GET',
    headers: createAuthHeaders()
  });
  const userChat = await userChatResponse.json();
  chatMessagesSpace.innerHTML = '';
  userChat.forEach(message => {
    displayMessage(message.senderId, message.content)
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
      roomId: selectedRoomId,
      participants: roomsDetails[selectedRoomId].participants,
      senderId: currentUser.id,
      content: messageContent,
    };
    stompClient.send("/app/chat", {}, JSON.stringify(Message));
    displayMessage(currentUser.id, messageContent);
    messageInput.value = '';
  }
  chatMessagesSpace.scrollTop = chatMessagesSpace.scrollHeight;
  event.preventDefault();
}

async function onMessageReceived(payload) {
  await fetchAndDisplayUsers();
  const message = JSON.parse(payload.body);
  // if the room is already open
  if (selectedRoomId && selectedRoomId === message.roomId) {
    displayMessage(message.senderId, message.content);
    chatMessagesSpace.scrollTop = chatMessagesSpace.scrollHeight;
  }

  if (selectedRoomId) {
    document.getElementById(`${selectedRoomId}`).classList.add('active');
  } else {
    messageForm.classList.add('hidden');
  }

  const notifiedUser = document.getElementById(`${message.roomId}`);
  if (notifiedUser && !notifiedUser.classList.contains('active')) {
    const nbrMsg = notifiedUser.querySelector('.nbr-msg');
    nbrMsg.classList.remove('hidden');
    nbrMsg.textContent = '';
  }
}

function onSearch() {
  document.querySelectorAll('.room-item').forEach(item => {
    item.classList.remove('active');
  });
  chatArea.classList.add("hidden");
  searchArea.classList.remove("hidden");

}

async function getUsers() {
  const searchContent = searchInput.value.trim();
  const usersList = document.getElementById('results');
  usersList.innerHTML = ''; // Clear previous results

  if (searchContent) {
    try {
      const usersResponse = await fetch(
          `/users/search?query=${searchContent}`, {
            method: 'GET',
            headers: createAuthHeaders()
          });

      if (!usersResponse.ok) {
        throw new Error(`Error: ${usersResponse.status}`);
      }

      let users = await usersResponse.json();

      users = users.filter(user => (user.id !== currentUser.id));

      if (users.length === 0) {
        const noResults = document.createElement('li');
        noResults.innerText = 'No users found';
        usersList.appendChild(noResults);
        return;
      }

      users.forEach((user, index) => {
        appendUserElement(user, usersList);

        if (index < users.length - 1) {
          const separator = document.createElement('li');
          separator.classList.add('separator');
          usersList.appendChild(separator);
        }
      });
    } catch (error) {
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

function appendUserElement(user, userList) {
  const listItem = document.createElement('li');
  const usernameSpan = document.createElement('span');
  const userImage = document.createElement('img');

  listItem.classList.add('room-item');
  listItem.id = user.id;

  userImage.src = '../images/user_icon.png';
  userImage.alt = user.username;
  usernameSpan.textContent = user.username;

  const receivedMsgs = document.createElement('span');
  receivedMsgs.textContent = '0';
  receivedMsgs.classList.add('nbr-msg', 'hidden');

  listItem.appendChild(userImage);
  listItem.appendChild(usernameSpan);
  listItem.appendChild(receivedMsgs);
  listItem.addEventListener('click', searchItemClick);
  userList.appendChild(listItem);
}

function searchItemClick(event) {
  let found = false;
  let activeRoom;

  const roomsList = document.getElementById('connectedUsers');
  const clickedUser = event.currentTarget;
  const selectedUserId = clickedUser.getAttribute('id');

  getRoom(selectedUserId).then(
      room => {
        roomsDetails[room.roomId] = room;
        selectedRoomId = room.roomId;
        if (roomsList) {
          for (let listItem of roomsList.children) {
            if (listItem.id === room.roomId) {
              activeRoom = listItem;
              found = true;
            }
          }
        }

        if (found === false) {
          activeRoom = document.createElement('li');

          activeRoom.classList.add('room-item');
          activeRoom.id = room.roomId;

          for (const child of clickedUser.children) {
            const clonedChild = child.cloneNode(true);
            activeRoom.appendChild(clonedChild);
          }
          roomsList.insertBefore(activeRoom, roomsList.firstChild)
        }

        messageForm.classList.remove('hidden');
        chatArea.classList.remove("hidden");
        searchArea.classList.add("hidden");
        activeRoom.classList.add('active');
        fetchAndDisplayUserChat().then();
        const nbrMsg = clickedUser.querySelector('.nbr-msg');
        nbrMsg.classList.add('hidden');
        nbrMsg.textContent = '0';
        activeRoom.addEventListener('click', roomItemClick);
      }
  ).catch(error => {
    console.error("couldn't get the room", error);
  })
}

async function getRoom(selectedUserId) {
  const Room = {
    type: "direct",
    participants: [selectedUserId, currentUser.id],
  }
  let roomResponse = await fetch("/room", {
    method: 'POST',
    headers: createAuthHeaders(),
    body: JSON.stringify(Room)
  })

  if (!roomResponse.ok) {
    throw new Error(`error with getting roomId ${roomResponse.status}`);
  }
  return await roomResponse.json();
}

function onLogout() {
  fetch("/auth/logout", {
    method: "POST",
    headers: createAuthHeaders()
  })
  .then(response => {
    if (response.ok) {
      window.location.href = "/login";
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