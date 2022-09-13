<!DOCTYPE html>
<html>
<head>
  <title>Authorization</title>
  <link rel="stylesheet" href="../static/css/login.css">
  <link rel="stylesheet" href="../static/css/main.css">
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Jost:wght@500&display=swap">
</head>
<body>
<div class="main">
  <input type="checkbox" id="chk" aria-hidden="true">

  <div class="signup">
    <form action="signup" method="post">
      <label for="chk" aria-hidden="true">Sign up</label>
      <label>
        <input class="user-input" type="text" name="name" placeholder="Full name" required>
      </label>
      <label>
        <input class="user-input" type="email" name="email" placeholder="Email" required>
      </label>
      <label>
        <input class="user-input" type="password" name="password" placeholder="Password" required>
      </label>
      <button class="control-button">Sign up</button>
    </form>
  </div>

  <div class="login">
    <form action="login" method="post">
      <label for="chk" aria-hidden="true">Login</label>
      <label>
        <input class="user-input" type="email" name="email" placeholder="Email" required>
      </label>
      <label>
        <input class="user-input" type="password" name="password" placeholder="Password" required>
      </label>
      <button class="control-button">Login</button>
    </form>
  </div>
</div>
</body>
</html>