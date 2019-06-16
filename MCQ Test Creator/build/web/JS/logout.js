$(document).ready(function()
{
    $("#logoutBtn").click(function()
    {
        $.ajax({
            url: 'Controller?operation=logout',
            type: 'POST',
            success: function(data)
            {
                data = Number(data);
                if(data === 1)
                {
                    location.href = "login.jsp";
                }
            }
        });
    });
});