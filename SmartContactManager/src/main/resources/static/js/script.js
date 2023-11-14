console.log("this");

const toggleSidebar = () => {

	if ($(".sidebar").is(":visible")) {

		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	}
	else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");

	}
};

function deleteContact(cid) {
	Swal.fire({
		title: "Are you sure?",
		text: "You want to delete this Contact!!",
		icon: "warning",
		showCancelButton: true,
		confirmButtonColor: "#3085d6",
		cancelButtonColor: "#d33",
		confirmButtonText: "Yes, delete it!"
	}).then((result) => {
		if (result.isConfirmed) {
			Swal.fire({
				title: "Deleted!",
				text: "Your file has been deleted.",
				icon: "success"
			});


			window.location.href = "/user/delete/" + cid;
		}
	})
};

const search = () => {
	// console.log("Searching...")

	let query = $("#search-input").val();

	if (query == '') {
		$(".search-result").hide();
	} else {
		console.log(query);

		let url = `http://localhost:8080/search/${query}`;

		fetch(url)
			.then((Response) => {
				return Response.json();
			})
			.then((data) => {
				console.log(data);

				let text=`<div class='list-group'>`;
				data.forEach((contact) =>{
					text+=`<a href=/user/contact/${contact.cid} class='list-group-item list-group-item-action'>${contact.name}</a>`;
				});
				text+=`</div>`

				$(".search-result").html(text);
				$(".search-result").show();
			});
	}
};



