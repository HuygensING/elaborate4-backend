# Project-specific configuration for elaborate_backend
# see http://cruisecontrol.huygens.knaw.nl/documentation/manual

Project.configure do |project|
  # Send email notifications about broken and fixed builds to email1@your.site, email2@your.site (default: send to nobody)
  project.email_notifier.emails = ['bram.buitendijk@huygens.knaw.nl']

  # Set email 'from' field to john@doe.com:
  # project.email_notifier.from = 'john@doe.com'

  # Build the project by invoking rake task 'custom'
  # project.rake_task = 'custom'

  # Build the project by invoking shell script "build_my_app.sh". Keep in mind that when the script is invoked, currentworking directory is
  # [cruise]/projects/your_project/work, so if you do not keep build_my_app.sh in version control, it should be '../build_my_app.sh' instead
  project.build_command = '~/bin/mvn_test.sh'

  # Ping Subversion for new revisions every 5 minutes (default: 30 seconds)
  project.scheduler.polling_interval = 5.minutes

  project.source_control = Subversion.new(:url => 'svn+ssh://svn@hi7dev.huygens.knaw.nl/data/svn/svn/repos/elaborate/trunk/elaborate_backend')
  project.do_clean_checkout :always
end
